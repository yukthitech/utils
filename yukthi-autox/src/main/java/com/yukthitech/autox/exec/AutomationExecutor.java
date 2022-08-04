package com.yukthitech.autox.exec;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseResult;
import com.yukthitech.autox.test.TestCaseValidationFailedException;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.autox.test.TestSuiteGroup;

public class AutomationExecutor
{
	private static Logger logger = LogManager.getLogger(AutomationExecutor.class);
	
	private AutomationContext context;
	
	private AutomationExecutorState state;
	
	private Consumer<Boolean> callback;
	
	public AutomationExecutor(AutomationContext context, TestSuiteGroup testSuites, Consumer<Boolean> callback)
	{
		this.context = context;
		this.state = new AutomationExecutorState(context);
		this.callback = callback;
	
		context.setAutomationExecutor(this);
		
		pushTestSuiteGroup(testSuites);
	}
	
	public void pushTestSuiteGroup(TestSuiteGroup testSuites)
	{
		ExecutionBranch mainBranch = testSuites.buildExecutionBranch(context);
		logger.debug("Got execution plan as:\n{}", mainBranch);
		
		state.pushBranch(new ExecutionStackEntry(mainBranch));
	}
	
	public StepExecutionBuilder newSteps(String label, Object executable, List<IStep> steps)
	{
		return new StepExecutionBuilder(this, label, executable, steps, ExecutionType.STEPS);
	}

	private StepExecutionBuilder newSteps(String label, ExecutionBranch branch, ExecutionType executionType)
	{
		return new StepExecutionBuilder(this, label, branch.executable, branch.getChildSteps(), executionType);
	}

	void pushSteps(ExecutionStackEntry stepEntry)
	{
		state.pushBranch(stepEntry);
	}

	public void start()
	{
		while(state.hasMoreBranches())
		{
			ExecutionStackEntry stackEntry = state.peekBranch();
			boolean res = false;
			boolean successul = true;
			
			if(stackEntry.isStepsEntry())
			{
				try
				{
					res = executeNextStep(stackEntry);
				}catch(ExecutionFlowFailed ex)
				{
					//when an execution flow fails, approp test case and others would have already been marked as errored/failed
					/// so here we moved to next item on stack
					continue;
				}
			}
			else
			{
				res = executeNextBranch(stackEntry);
				
				TestCaseResult result = stackEntry.getBranch().result;

				if(result != null && result.getStatus() != TestStatus.SUCCESSFUL)
				{
					successul = false;
				}
			}
			
			if(res)
			{
				stackEntry.completed(successul);
				state.popBranch();
			}
		}
		
		this.state.automationCompleted();
		callback.accept(state.isSuccessful());
	}
	
	private boolean executeNextStep(ExecutionStackEntry stackEntry) throws ExecutionFlowFailed
	{
		List<IStep> steps = stackEntry.getSteps();
		int idx = stackEntry.getChildStepIndex();
		
		if(idx >= steps.size())
		{
			if(!stackEntry.isReexecutionNeeded())
			{
				return true;
			}
			
			stackEntry.resetChildIndex();
			idx = 0;
		}
		
		if(idx == 0)
		{
			stackEntry.started();
		}
		
		IStep step = steps.get(idx);
		
		//on completion of step execution, move to next step
		if(executeStep(step, stackEntry))
		{
			stackEntry.incrementChildStepIndex();
		}
		
		return false;
	}
	
	/**
	 * Executes the step. And returns true if step execution is completed. In case step needs to be re-executed 
	 * this will return false.
	 * 
	 * @param step
	 */
	private boolean executeStep(IStep sourceStep, ExecutionStackEntry parentStackEntry) throws ExecutionFlowFailed
	{
		//If a step pushes child steps/branches, then it will be kept on stack, till
		// child is completed. Running step check is used to ensure the step is executed only once
		if(parentStackEntry.isRunningStep(sourceStep))
		{
			//for already executing step, simply remove from execution stacks
			parentStackEntry.stepCompleted(sourceStep, true, null);
			context.getExecutionStack().pop(sourceStep);
			return true;
		}
		
		ExecutionLogger exeLogger = this.state.getExecutionLogger();
		
		//if step is marked not to log anything
		if(sourceStep.isLoggingDisabled())
		{
			//disable logging
			exeLogger.setDisabled(true);
		}
		
		context.getExecutionStack().push(sourceStep);
		
		//initialize step with source step to support error handling
		IStep step = sourceStep;
		boolean childBranchPushed = false;
		boolean exceptionOccurred = false;
		
		try
		{

			context.setExecutionLogger(exeLogger);

			parentStackEntry.stepPreStart(step);
			
			//clone the step, so that expression replacement will not affect actual step
			step = sourceStep.clone();
			AutomationUtils.replaceExpressions("step-" + step.getClass().getName(), context, step);
			step.setSourceStep(sourceStep);
			
			parentStackEntry.stepStarted(step);
			
			try
			{
				step.execute(context, exeLogger);
			}catch(AutoxValidationException ex)
			{
				throw new TestCaseValidationFailedException(step, ex.getMessage());
			}
			
			childBranchPushed = context.getExecutionStack().isNotPeekElement(step);

			if(childBranchPushed)
			{
				return false;
			}
			
			parentStackEntry.stepCompleted(step, true, null);
			return true;
		} catch(ExecutionFlowFailed ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			exceptionOccurred = true;
			
			//Note, with exception, it will be assumed no child branches/steps are pushed
			boolean exceptionHandled = this.state.handleException(step, ex);
			
			//Note: During exception handling, based on if exception is handled by
			//  current step or child step, the current step would have been popped.
			// So, pop the step only if it is there on stack
			if(context.getExecutionStack().isPeekElement(step))
			{
				parentStackEntry.stepCompleted(step, exceptionHandled, ex);
				context.getExecutionStack().pop(step);
			}
			
			//if exception is handled, then simply return from current execution
			if(exceptionHandled)
			{
				return true;
			}
			
			throw new ExecutionFlowFailed(step, ex.getMessage(), ex);
		} finally
		{
			//when child branch is pushed or exception is occurred, don't remove current step from stack
			// Note: As part of exception handling running steps would be removed.
			if(!childBranchPushed && !exceptionOccurred)
			{
				context.getExecutionStack().pop(step);
			}
			
			//re-enable logging, in case it is disabled
			exeLogger.setDisabled(false);
			context.setExecutionLogger(null);
		}
	}
	
	private boolean executeNextBranch(ExecutionStackEntry stackEntry)
	{
		ExecutionBranch branch = stackEntry.getBranch();
		ExecutionStackEntry parentEntry = this.state.parentBranch();
		
		if(!stackEntry.isStarted())
		{
			logger.debug("Starting execution of branch: {}", branch.label);
			stackEntry.started();
			this.state.started(branch);
		}
		
		if(CollectionUtils.isNotEmpty(branch.dependencies))
		{
			for(String dep : branch.dependencies)
			{
				if(!this.state.isCompleted(dep))
				{
					Date time = new Date();
					branch.result = new TestCaseResult((TestCase) branch.executable, branch.label, TestStatus.SKIPPED, null, "Skipping as dependency test case was not successul: " + dep,
							time, time);
					
					this.state.completedBranchEntry(stackEntry);
					return true;
				}
			}
		}

		//Note: before-child & after-child flags are maintained at child level so that 
		//  before and after steps are executed for every child
		if(!stackEntry.isBeforeChildPushed())
		{
			stackEntry.setBeforeChildPushed(true);
			
			ExecutionBranch parentBranch = parentEntry != null ? parentEntry.getBranch() : null;
			
			if(parentBranch != null && parentBranch.beforeChild != null)
			{
				newSteps(parentBranch.beforeChild.getLabel(), parentBranch.beforeChild, ExecutionType.PRE_CHILD)
					.onSimpleInit(entry -> state.startMode("prechild"))
					.onSuccess(entry -> state.clearMode())
					.execute();
				
				return false;
			}
		}

		if(!stackEntry.isSetupPushed())
		{
			stackEntry.setSetupPushed(true);
			
			if(branch.setup != null)
			{
				//create a new dynamic branch for setting callback
				newSteps(branch.getSetup().getLabel(), branch.getSetup(), ExecutionType.SETUP)
					.onPreexecute(entry -> state.preSetup(branch))
					.onSuccess(entry -> state.postSetup((ExecutionStackEntry) entry, branch, true))
					.execute();
				
				return false;
			}
		}
		
		if(branch.dataProvider != null && !stackEntry.isDataSetupPushed())
		{
			stackEntry.setDataSetupPushed(true);
			
			if(branch.dataSetup != null)
			{
				newSteps(branch.dataSetup.getLabel(), branch.dataSetup, ExecutionType.DATA_SETUP)
					.onSimpleInit(entry -> state.startMode("dataSetup"))
					.onSuccess(entry -> state.testCasePostDataStartup((TestCase) branch.getExecutable(), branch))
					.execute();
				return false;
			}
			else
			{
				state.testCasePostDataStartup((TestCase) stackEntry.getExecutable(), branch);
				return false;
			}
		}

		if(!stackEntry.isBranchesPushed())
		{
			int childBrnchIdx = stackEntry.getChildBranchIndex();
			state.pushBranch(new ExecutionStackEntry(branch.getChildBranches().get(childBrnchIdx)));
			stackEntry.incrementChildBranchIndex();
			
			return false;
		}
		
		//Note: for data provider branch steps should not be executed directly
		if(branch.dataProvider == null && !stackEntry.isStepsPushed())
		{
			stackEntry.setStepsPushed(true);
			
			if(CollectionUtils.isNotEmpty(branch.childSteps))
			{
				newSteps(branch.getLabel() + " - Steps", null, branch.getChildSteps())
					.onSimpleInit(entry -> state.clearMode())
					.execute();
				
				return false;
			}
		}
		
		if(branch.dataProvider != null && !stackEntry.isDataCleanupPushed())
		{
			stackEntry.setDataCleanupPushed(true);
			
			if(branch.dataCleanup != null)
			{
				newSteps(branch.dataCleanup.getLabel(), branch.dataCleanup, ExecutionType.DATA_CLEANUP)
					.onSimpleInit(entry -> state.startMode("dataCleanup"))
					.execute();
				
				return false;
			}
		}

		//execute cleanup only when setup is executed successfully
		if(!stackEntry.isCleanupPushed())
		{
			stackEntry.setCleanupPushed(true);
			
			if(branch.cleanup != null)
			{
				newSteps(branch.getCleanup().getLabel(), branch.getCleanup(), ExecutionType.CLEANUP)
					.onPreexecute(entry -> state.preCleanup(branch))
					.onSuccess(entry -> state.postCleanup((ExecutionStackEntry) entry, branch, true))
					.execute();
				return false;
			}
			else
			{
				state.postCleanup(null, branch, true);
				return false;
			}
		}

		//Note: before-child & after-child flags are maintained at child level so that 
		//  before and after steps are executed for every child
		if(!stackEntry.isAfterChildPushed())
		{
			stackEntry.setAfterChildPushed(true);
			
			ExecutionBranch parentBranch = parentEntry != null ? parentEntry.getBranch() : null;

			if(parentBranch != null && parentBranch.afterChild != null)
			{
				newSteps(parentBranch.afterChild.getLabel(), parentBranch.afterChild, ExecutionType.POST_CHILD)
					.onSimpleInit(entry -> state.startMode("postchild"))
					.onSuccess(entry -> state.clearMode())
					.execute();
				
				return false;
			}
		}

		this.state.completedBranchEntry(stackEntry);
		return true;
	}
}
