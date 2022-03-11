package com.yukthitech.autox.exec;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.utils.CommonUtils;

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
	
	private boolean dataSetupPushed;

	private boolean stepsPushed;
	
	private boolean cleanupPushed;
	
	private boolean dataCleanupPushed;

	private int childStepIndex = 0;
	
	private int childBranchIndex = 0;
	
	private Consumer<ExecutionStackEntry> onSuccess;
	
	private Consumer<ExecutionStackEntry> onInit;
	
	private Consumer<ExecutionStackEntry> onComplete;
	
	private ExceptionHandler exceptionHandler;
	
	private Date startedOn;
	
	private boolean beforeChildPushed;
	
	private boolean afterChildPushed;
	
	private Object executable;
	
	private ExecutionLogger executionLogger;
	
	private ExecutionStackEntry parent;

	public ExecutionStackEntry(ExecutionBranch branch)
	{
		this.branch = branch;
		this.label = branch.label;
		this.executable = branch.executable;
	}
	
	public ExecutionStackEntry(String label, Object executable, List<IStep> steps)
	{
		this.steps = steps;
		this.label = label;
		this.executable = executable;
	}
	
	public void setParent(ExecutionStackEntry parent)
	{
		this.parent = parent;
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
		List<ExecutionBranch> childBranches = branch.getChildBranches();
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

	public void setOnInit(Consumer<ExecutionStackEntry> onInit)
	{
		this.onInit = onInit;
	}
	
	public Consumer<ExecutionStackEntry> getOnInit()
	{
		return onInit;
	}
	
	public void setOnComplete(Consumer<ExecutionStackEntry> onComplete)
	{
		this.onComplete = onComplete;
	}
	
	public Consumer<ExecutionStackEntry> getOnComplete()
	{
		return onComplete;
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
	
	public boolean isDataSetupPushed()
	{
		return dataSetupPushed;
	}

	public void setDataSetupPushed(boolean dataSetupPushed)
	{
		this.dataSetupPushed = dataSetupPushed;
	}

	public boolean isDataCleanupPushed()
	{
		return dataCleanupPushed;
	}

	public void setDataCleanupPushed(boolean dataCleanupPushed)
	{
		this.dataCleanupPushed = dataCleanupPushed;
	}

	public ExecutionLogger getExecutionLogger()
	{
		return executionLogger;
	}

	public void setExecutionLogger(ExecutionLogger executionLogger)
	{
		this.executionLogger = executionLogger;
	}
	
	public Object getExecutable()
	{
		return executable;
	}
	
	public String getLabel()
	{
		return label;
	}

	public void completedBranch(boolean successful)
	{
		if(successful && onSuccess != null)
		{
			onSuccess.accept(this);
		}
		
		if(onComplete != null)
		{
			onComplete.accept(this);
		}
	}
	
	public ExecutionStackEntry getParentEntry(Class<?>... ofTypes)
	{
		Set<Class<?>> ofTypeSet = CommonUtils.toSet(ofTypes);
		ExecutionStackEntry curEntry = this;
		
		while(curEntry != null)
		{
			if(curEntry.executable != null && ofTypeSet.contains(curEntry.executable.getClass()))
			{
				return curEntry;
			}
			
			curEntry = curEntry.parent;
		}
		
		return null;
	}

	@Override
	public String toString()
	{
		return super.toString() + "[" + label + "]";
	}
}
