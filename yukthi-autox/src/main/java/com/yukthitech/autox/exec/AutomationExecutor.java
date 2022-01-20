package com.yukthitech.autox.exec;

import java.util.List;
import java.util.Stack;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.test.TestSuiteGroup;

import net.sourceforge.jaad.aac.tools.IS;

public class AutomationExecutor
{
	private AutomationContext context;
	
	private Stack<BranchExecutionState> branchStack = new Stack<>();
	
	public AutomationExecutor(AutomationContext context, TestSuiteGroup testSuites)
	{
		this.context = context;
		
		ExecutionBranch mainBranch = testSuites.buildExecutionBranch(context);
		branchStack.push(new BranchExecutionState(mainBranch));
	}
	
	public void start()
	{
		while(!branchStack.isEmpty())
		{
			BranchExecutionState state = branchStack.peek();
			
			try
			{
				executeNext(state);
			}catch(PauseExecutionException ex)
			{
				
			}
		}
	}
	
	private void executeNext(BranchExecutionState state) throws PauseExecutionException
	{
		if(state.isSetupCompleted())
		{
			state.setSetupCompleted(true);
			
			if(state.getBranch().setup != null)
			{
				BranchExecutionState setupState = new BranchExecutionState(state.getBranch().setup);
				branchStack.push(setupState);

				executeNext(setupState);
			}
		}
		
		ExecutionBranch branch = state.getBranch();
		
		if(CollectionUtils.isNotEmpty(branch.childBranches))
		{
			List<ExecutionBranch> childBranches = state.getBranch().childBranches;
			int i = state.getChildBranchIndex();
			int size = childBranches.size();
			
			for( ; i < size; i++)
			{
				ExecutionBranch nextBranch = childBranches.get(i);
				BranchExecutionState nextState = new BranchExecutionState(nextBranch);
				branchStack.push(nextState);
				
				executeNext(nextState);
			}
		}

		if(CollectionUtils.isNotEmpty(branch.childSteps))
		{
			List<IStep> childSteps = state.getBranch().childSteps;
			int i = state.getChildStepIndex();
			int size = childSteps.size();
			
			for( ; i < size; i++)
			{
				IStep nextStep = childSteps.get(i);
				execueStep(nextStep);
			}
		}
	}
	
	private void execueStep(IStep step)
	{
		
	}
}
