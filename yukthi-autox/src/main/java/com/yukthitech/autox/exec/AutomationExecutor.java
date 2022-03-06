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
	
	public StepExecutionBuilder newSteps(String label, List<IStep> steps)
	{
		return new StepExecutionBuilder(this, label, steps);
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
				state.popBranch();
				
				if(stackEntry.getOnSuccess() != null)
				{
					stackEntry.getOnSuccess().accept(stackEntry);
				}
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
		
		if(executeStep(step))
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
	private boolean executeStep(IStep step) throws ExecutionFlowFailed
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
			logger.debug("Starting execution branch: {}", branch.label);
			stackEntry.started();
			this.state.started(branch);
		}
		
		//Note: before-child & after-child flags are maintained at child level so that 
		//  before and after steps are executed for every child
		if(!branch.dataBranch && !stackEntry.isBeforeChildPushed())
		{
			stackEntry.setBeforeChildPushed(true);
			
			ExecutionBranch parentBranch = parentEntry != null ? parentEntry.getBranch() : null;
			
			if(parentBranch != null && parentBranch.beforeChild != null)
			{
				this.state.startMode("prechild");
				
				newSteps(parentBranch.beforeChild.getLabel(), parentBranch.beforeChild.getChildSteps())
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
				this.state.preSetup(branch);
				
				//create a new dynamic branch for setting callback
				newSteps(branch.getSetup().getLabel(), branch.getSetup().getChildSteps())
				.onSuccess(entry -> state.postSetup(entry, branch, true))
				.push();
				
				return false;
			}
		}
		
		if(!stackEntry.isBranchesPushed())
		{
			int childBrnchIdx = stackEntry.getChildBranchIndex();
			state.pushBranch(new ExecutionStackEntry(branch.childBranches.get(childBrnchIdx)));
			stackEntry.incrementChildBranchIndex();
			
			return false;
		}
		
		//Note: for data branch steps should not be executed directly
		if(!branch.dataBranch && !stackEntry.isStepsPushed())
		{
			stackEntry.setStepsPushed(true);
			
			if(CollectionUtils.isNotEmpty(branch.childSteps))
			{
				newSteps(branch.getLabel() + " - Steps", branch.getChildSteps()).push();
				return false;
			}
		}
		
		//execute cleanup only when setup is executed successfully
		if(!stackEntry.isCleanupPushed())
		{
			stackEntry.setCleanupPushed(true);
			
			if(branch.cleanup != null)
			{
				this.state.preCleanup(branch);
				
				newSteps(branch.getCleanup().getLabel(), branch.getCleanup().getChildSteps())
					.onSuccess(entry -> state.postCleanup(entry, branch, true))
					.push();
				return false;
			}
		}

		//Note: before-child & after-child flags are maintained at child level so that 
		//  before and after steps are executed for every child
		if(!branch.dataBranch && !stackEntry.isAfterChildPushed())
		{
			stackEntry.setAfterChildPushed(true);
			
			ExecutionBranch parentBranch = parentEntry != null ? parentEntry.getBranch() : null;

			if(parentBranch != null && parentBranch.afterChild != null)
			{
				this.state.startMode("postchild");
				
				newSteps(parentBranch.afterChild.getLabel(), parentBranch.afterChild.getChildSteps())
				.onSuccess(entry -> state.clearMode())
				.push();
				
				return false;
			}
		}

		this.state.completed(stackEntry);
		return true;
	}
}
