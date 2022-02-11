package com.yukthitech.autox.exec;

import java.util.List;

import com.yukthitech.autox.IStep;

/**
 * Execution state of branch.
 * @author akranthikiran
 */
public class ExecutionStackEntry
{
	/**
	 * Branch whose state being managed.
	 */
	private ExecutionBranch branch;
	
	private boolean started;
	
	private List<IStep> steps;
	
	private boolean setupPushed;
	
	private boolean setupCompleted;
	
	private boolean branchesPushed;
	
	private boolean stepsPushed;
	
	private boolean cleanupPushed;
	
	private int childStepIndex = 0;
	
	private Runnable onSuccess;
	
	public ExecutionStackEntry(ExecutionBranch branch)
	{
		this.branch = branch;
	}
	
	public ExecutionStackEntry(List<IStep> steps)
	{
		this.steps = steps;
	}
	
	public boolean isStarted()
	{
		return started;
	}

	public void setStarted(boolean started)
	{
		this.started = started;
	}

	public boolean isStepsState()
	{
		return (steps != null);
	}

	public ExecutionBranch getBranch()
	{
		return branch;
	}

	public boolean isSetupPushed()
	{
		return setupPushed;
	}

	public void setSetupPushed(boolean setupPushed)
	{
		this.setupPushed = setupPushed;
	}

	public boolean isBranchesPushed()
	{
		return branchesPushed;
	}

	public void setBranchesPushed(boolean branchesPushed)
	{
		this.branchesPushed = branchesPushed;
	}

	public boolean isStepsPushed()
	{
		return stepsPushed;
	}

	public void setStepsPushed(boolean stepsPushed)
	{
		this.stepsPushed = stepsPushed;
	}

	public boolean isCleanupPushed()
	{
		return cleanupPushed;
	}

	public void setCleanupPushed(boolean cleanupPushed)
	{
		this.cleanupPushed = cleanupPushed;
	}

	public int getChildStepIndex()
	{
		return childStepIndex;
	}

	public void setChildStepIndex(int childStepIndex)
	{
		this.childStepIndex = childStepIndex;
	}

	public List<IStep> getSteps()
	{
		return steps;
	}

	public Runnable getOnSuccess()
	{
		return onSuccess;
	}

	public void setOnSuccess(Runnable onSuccess)
	{
		this.onSuccess = onSuccess;
	}

	public boolean isSetupCompleted()
	{
		return setupCompleted;
	}

	public void setSetupCompleted(boolean setupCompleted)
	{
		this.setupCompleted = setupCompleted;
	}
}
