package com.yukthitech.autox.exec;

/**
 * Execution state of branch.
 * @author akranthikiran
 */
public class BranchExecutionState
{
	/**
	 * Branch whose state being managed.
	 */
	private ExecutionBranch branch;
	
	private boolean setupCompleted;
	
	private int childBranchIndex = 0;
	
	private int childStepIndex = 0;
	
	private boolean successful = true;
	
	private boolean cleanupExecuted;

	public BranchExecutionState(ExecutionBranch branch)
	{
		this.branch = branch;
	}

	public ExecutionBranch getBranch()
	{
		return branch;
	}

	public void setBranch(ExecutionBranch branch)
	{
		this.branch = branch;
	}

	public boolean isSetupCompleted()
	{
		return setupCompleted;
	}

	public void setSetupCompleted(boolean setupCompleted)
	{
		this.setupCompleted = setupCompleted;
	}

	public int getChildBranchIndex()
	{
		return childBranchIndex;
	}

	public void setChildBranchIndex(int childBranchIndex)
	{
		this.childBranchIndex = childBranchIndex;
	}

	public int getChildStepIndex()
	{
		return childStepIndex;
	}

	public void setChildStepIndex(int childStepIndex)
	{
		this.childStepIndex = childStepIndex;
	}

	public boolean isSuccessful()
	{
		return successful;
	}

	public void setSuccessful(boolean successful)
	{
		this.successful = successful;
	}

	public boolean isCleanupExecuted()
	{
		return cleanupExecuted;
	}

	public void setCleanupExecuted(boolean cleanupExecuted)
	{
		this.cleanupExecuted = cleanupExecuted;
	}
}
