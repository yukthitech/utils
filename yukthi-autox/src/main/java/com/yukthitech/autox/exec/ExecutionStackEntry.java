package com.yukthitech.autox.exec;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;

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
	
	private String label;
	
	private boolean started;
	
	private List<IStep> steps;
	
	private boolean setupPushed;
	
	private boolean stepsPushed;
	
	private boolean cleanupPushed;
	
	private int childStepIndex = 0;
	
	private int childBranchIndex = 0;
	
	private Consumer<ExecutionStackEntry> onSuccess;
	
	private ExceptionHandler exceptionHandler;
	
	private Date startedOn;
	
	private boolean beforeChildPushed;
	
	private boolean afterChildPushed;
	
	public ExecutionStackEntry(ExecutionBranch branch)
	{
		this.branch = branch;
		this.label = branch.label;
	}
	
	public ExecutionStackEntry(String label, List<IStep> steps)
	{
		this.steps = steps;
		this.label = label;
	}
	
	public boolean isStarted()
	{
		return started;
	}
	
	public void started()
	{
		this.started = true;
		startedOn = new Date();
	}
	
	public Date getStartedOn()
	{
		return startedOn;
	}

	public boolean isStepsEntry()
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
		List<ExecutionBranch> childBranches = branch.childBranches;
		return (CollectionUtils.isEmpty(childBranches) || childBranchIndex >= childBranches.size());
	}
	
	public void incrementChildBranchIndex()
	{
		childBranchIndex++;
	}
	
	public int getChildBranchIndex()
	{
		return childBranchIndex;
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
	
	public void incrementChildStepIndex()
	{
		childStepIndex++;
	}

	public void setChildStepIndex(int childStepIndex)
	{
		this.childStepIndex = childStepIndex;
	}

	public List<IStep> getSteps()
	{
		return steps;
	}

	public Consumer<ExecutionStackEntry> getOnSuccess()
	{
		return onSuccess;
	}

	public void setOnSuccess(Consumer<ExecutionStackEntry> onSuccess)
	{
		this.onSuccess = onSuccess;
	}
	
	public void setExceptionHandler(ExceptionHandler exceptionHandler)
	{
		this.exceptionHandler = exceptionHandler;
	}
	
	public ExceptionHandler getExceptionHandler()
	{
		return exceptionHandler;
	}

	public boolean isBeforeChildPushed()
	{
		return beforeChildPushed;
	}

	public void setBeforeChildPushed(boolean beforeChildPushed)
	{
		this.beforeChildPushed = beforeChildPushed;
	}

	public boolean isAfterChildPushed()
	{
		return afterChildPushed;
	}

	public void setAfterChildPushed(boolean afterChildPushed)
	{
		this.afterChildPushed = afterChildPushed;
	}
}
