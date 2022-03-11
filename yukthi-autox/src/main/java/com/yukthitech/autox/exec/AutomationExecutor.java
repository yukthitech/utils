package com.yukthitech.autox.exec;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.test.TestCaseValidationFailedException;
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
		
		ExecutionBranch mainBranch = testSuites.buildExecutionBranch(context);
		logger.debug("Got execution plan as:\n{}", mainBranch);
		
		state.pushBranch(new ExecutionStackEntry(mainBranch));
	}
	
	public StepExecutionBuilder newSteps(String label, Object executable, List<IStep> steps)
	{
		return new StepExecutionBuilder(this, label, executable, steps);
	}

	private StepExecutionBuilder newSteps(String label, ExecutionBranch branch)
	{
		return new StepExecutionBuilder(this, label, branch.executable, branch.getChildSteps());
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
			
			if(stackEntry.isStepsEntry())
			{
				try
				{
					res = executeNextStep(stackEntry);
				}catch(ExecutionFlowFailed ex)
				{
					this.state.handleException(stackEntry, ex);
					continue;
				}
			}
			else
			{
				res = executeNextBranch(stackEntry);
			}
			
			if(res)
			{
				stackEntry.completedBranch(true);
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
			return true;
		}
		
		if(idx == 0)
		{
			state.startedSteps(stackEntry.getBranch());
		}
		
		IStep step = steps.get(idx);
		
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
	 * @return should return true, if the ste
	 */
	private boolean executeStep(IStep step, ExecutionStackEntry stackEntry) throws ExecutionFlowFailed
	{
		//TODO: Check how to understand if step is completed or not.
		//TODO: How to throw exception to high level branches on the stack.
		
		ExecutionLogger exeLogger = this.state.getExecutionLogger();
		
		//if step is marked not to log anything
		if(step.isLoggingDisabled())
		{
			//disable logging
			exeLogger.setDisabled(true);
		}
		
		context.getExecutionStack().push(step);
		
		try
		{

			context.setExecutionLogger(exeLogger);
			
			//clone the step, so that expression replacement will not affect actual step
			step = step.clone();
			AutomationUtils.replaceExpressions("step-" + step.getClass().getName(), context, step);
	
			context.getStepListenerProxy().stepStarted(step, null);
			boolean res = step.execute(context, exeLogger);
	
			if(step instanceof IValidation)
			{
				if(!res)
				{
					Executable executable = step.getClass().getAnnotation(Executable.class);
					
					String message = String.format("Validation %s failed. Validation Details: %s", executable.name(), step);
					
					//exeLogger.error(message);
					throw new TestCaseValidationFailedException(step, message);
				}
			}
		
			context.getStepListenerProxy().stepCompleted(step, null);
		} catch(Exception ex)
		{
			context.getStepListenerProxy().stepErrored(step, null, ex);
			
			//exeLogger.error("An error occurred with message - {}. Stack Trace: {}", ex.getMessage(), context.getExecutionStack().toStackTrace());
			throw new ExecutionFlowFailed(step, ex.getMessage(), ex);
		} finally
		{
			context.getExecutionStack().pop(step);
			
			//re-enable logging, in case it is disabled
			exeLogger.setDisabled(false);
			context.setExecutionLogger(null);
		}
		
		return true;
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
		
		//Note: before-child & after-child flags are maintained at child level so that 
		//  before and after steps are executed for every child
		if(!stackEntry.isBeforeChildPushed())
		{
			stackEntry.setBeforeChildPushed(true);
			
			ExecutionBranch parentBranch = parentEntry != null ? parentEntry.getBranch() : null;
			
			if(parentBranch != null && parentBranch.beforeChild != null)
			{
				newSteps(parentBranch.beforeChild.getLabel(), parentBranch.beforeChild)
					.onInit(entry -> state.startMode("prechild"))
					.onSuccess(entry -> state.clearMode())
					.push();
				
				return false;
			}
		}

		if(!stackEntry.isSetupPushed())
		{
			stackEntry.setSetupPushed(true);
			
			if(branch.setup != null)
			{
				
				//create a new dynamic branch for setting callback
				newSteps(branch.getSetup().getLabel(), branch.getSetup())
					.onInit(entry -> state.preSetup(branch))
					.onSuccess(entry -> state.postSetup(entry, branch, true))
					.push();
				
				return false;
			}
			else
			{
				state.postSetup(null, branch, true);
				return false;
			}
		}
		
		if(branch.dataProvider != null && !stackEntry.isDataSetupPushed())
		{
			stackEntry.setDataSetupPushed(true);
			
			if(branch.dataSetup != null)
			{
				newSteps(branch.dataSetup.getLabel(), branch.dataSetup)
					.onInit(entry -> state.startMode("dataSetup"))
					.push();
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
					.onInit(entry -> state.clearMode())
					.push();
				
				return false;
			}
		}
		
		if(branch.dataProvider != null && !stackEntry.isDataCleanupPushed())
		{
			stackEntry.setDataCleanupPushed(true);
			
			if(branch.dataCleanup != null)
			{
				newSteps(branch.dataCleanup.getLabel(), branch.dataCleanup)
					.onInit(entry -> state.startMode("dataCleanup"))
					.push();
				
				return false;
			}
		}

		//execute cleanup only when setup is executed successfully
		if(!stackEntry.isCleanupPushed())
		{
			stackEntry.setCleanupPushed(true);
			
			if(branch.cleanup != null)
			{
				newSteps(branch.getCleanup().getLabel(), branch.getCleanup())
					.onInit(entry -> state.preCleanup(branch))
					.onSuccess(entry -> state.postCleanup(entry, branch, true))
					.push();
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
				newSteps(parentBranch.afterChild.getLabel(), parentBranch.afterChild)
					.onInit(entry -> state.startMode("postchild"))
					.onSuccess(entry -> state.clearMode())
					.push();
				
				return false;
			}
		}

		this.state.completed(stackEntry);
		return true;
	}
}
