package com.yukthitech.autox.exec;

import java.util.List;
import java.util.Stack;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.test.TestCaseValidationFailedException;
import com.yukthitech.autox.test.TestSuiteGroup;

import net.sourceforge.jaad.aac.tools.IS;

public class AutomationExecutor
{
	private AutomationContext context;
	
	private Stack<ExecutionStackEntry> branchStack = new Stack<>();
	
	private AutomationExecutorState automationExecutorState;
	
	public AutomationExecutor(AutomationContext context, TestSuiteGroup testSuites)
	{
		this.context = context;
		this.automationExecutorState = new AutomationExecutorState(context);
		
		ExecutionBranch mainBranch = testSuites.buildExecutionBranch(context);
		branchStack.push(new ExecutionStackEntry(mainBranch));
	}
	
	public void start()
	{
		while(!branchStack.isEmpty())
		{
			ExecutionStackEntry state = branchStack.peek();
			boolean res = false;
			
			if(state.isStepsState())
			{
				try
				{
					res = executeNextStep(state);
				}catch(PauseExecutionException ex)
				{
					
				}
			}
			else
			{
				res = executeNextBranch(state);
			}
			
			if(res)
			{
				branchStack.pop();
			}
		}
	}
	
	public ExecutionStackEntry pushSteps(List<IStep> steps)
	{
		if(CollectionUtils.isEmpty(steps))
		{
			return null;
		}
		
		ExecutionStackEntry stackEntry = new ExecutionStackEntry(steps);
		branchStack.push(stackEntry);
		
		return stackEntry;
	}
	
	private boolean executeNextStep(ExecutionStackEntry state) throws PauseExecutionException
	{
		List<IStep> steps = state.getSteps();
		int idx = state.getChildStepIndex();
		
		if(idx >= steps.size())
		{
			return true;
		}
		
		IStep step = steps.get(idx);
		
		return true;
	}
	
	private void executeStep(IStep step)
	{
		//clone the step, so that expression replacement will not affect actual step
		step = step.clone();
		AutomationUtils.replaceExpressions("step-" + step.getClass().getName(), context, step);

		context.getStepListenerProxy().stepStarted(step, null);
		boolean res = false;//step.execute(context, null);

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
		
		//context.getStepListenerProxy().stepCompleted(step, currentData);
	}
	
	private boolean executeNextBranch(ExecutionStackEntry stackEntry)
	{
		ExecutionBranch branch = stackEntry.getBranch();
		
		if(!stackEntry.isStarted())
		{
			stackEntry.setStarted(true);
			this.automationExecutorState.started(branch);
		}
		
		if(!stackEntry.isSetupPushed())
		{
			stackEntry.setSetupPushed(true);
			
			if(branch.setup != null)
			{
				ExecutionStackEntry childEntry = pushSteps(branch.getSetup().getChildSteps());
				childEntry.setOnSuccess(() -> stackEntry.setSetupCompleted(true));
				return false;
			}
		}
		
		if(!stackEntry.isBranchesPushed())
		{
			stackEntry.setBranchesPushed(true);

			//in case child branches are present, push in reverse order, 
			//  so that first one will be picked first
			if(CollectionUtils.isNotEmpty(branch.childBranches))
			{
				int maxIdx = branch.childBranches.size() - 1;
				
				for(int i = maxIdx; i >= 0; i--)
				{
					branchStack.push(new ExecutionStackEntry(branch.childBranches.get(i)));
				}
			}
			
			return false;
		}
		
		if(!stackEntry.isStepsPushed())
		{
			if(CollectionUtils.isNotEmpty(branch.childSteps))
			{
				pushSteps(branch.getSetup().getChildSteps());
			}
			
			return false;
		}
		
		if(!stackEntry.isCleanupPushed())
		{
			stackEntry.setCleanupPushed(true);
			
			//execute cleanup only when setup is executed successfully
			if(branch.cleanup != null && stackEntry.isSetupCompleted())
			{
				pushSteps(branch.getCleanup().getChildSteps());
				return false;
			}
		}
		
		return true;
	}
	
	private void execueStep(IStep step)
	{
		
	}
}
