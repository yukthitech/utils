package com.yukthitech.autox.exec;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepListener;
import com.yukthitech.utils.CommonUtils;

/**
 * Execution state of branch.
 * @author akranthikiran
 */
class ExecutionStackEntry implements IExecutionStackEntry
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
	
	private Consumer<IExecutionStackEntry> onSuccess;
	
	/**
	 * Init callback, which will be called first time before execution of stack entry. 
	 * If this returns false, then stack entry will not be executed and will be skipped.
	 */
	private Function<IExecutionStackEntry, Boolean> onInit;
	
	private Consumer<IExecutionStackEntry> preexecute;
	
	private Consumer<IExecutionStackEntry> onComplete;
	
	private Function<IExecutionStackEntry, Boolean> reexecutionNeeded;
	
	private IExceptionHandler exceptionHandler;
	
	private Date startedOn;
	
	private boolean beforeChildPushed;
	
	private boolean afterChildPushed;
	
	private Object executable;
	
	private ExecutionLogger executionLogger;
	
	private ExecutionStackEntry parent;
	
	private ExecutionType executionType;
	
	private IStepListener stepListener;
	
	private Map<String, Object> stackVariables;
	
	public ExecutionStackEntry(ExecutionBranch branch)
	{
		this.branch = branch;
		this.label = branch.label;
		this.executable = branch.executable;
	}
	
	public ExecutionStackEntry(String label, Object executable, List<IStep> steps, ExecutionType executionType)
	{
		this.steps = steps;
		this.label = label;
		this.executable = executable;
		this.executionType = executionType;
	}
	
	public void setStepListener(IStepListener stepListener)
	{
		this.stepListener = stepListener;
	}
	
	public ExecutionType getExecutionType()
	{
		return executionType;
	}
	
	public void setParent(ExecutionStackEntry parent)
	{
		this.parent = parent;
	}
	
	public ExecutionStackEntry getParent()
	{
		return parent;
	}
	
	public boolean isStarted()
	{
		return started;
	}
	
	public void started()
	{
		this.started = true;
		startedOn = new Date();
		
		if(preexecute == null)
		{
			return;
		}
		
		preexecute.accept(this);
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
	
	public void resetChildIndex()
	{
		childStepIndex = 0;
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

	public Consumer<IExecutionStackEntry> getOnSuccess()
	{
		return onSuccess;
	}

	public void setOnSuccess(Consumer<IExecutionStackEntry> onSuccess)
	{
		this.onSuccess = onSuccess;
	}
	
	public void setPreexecute(Consumer<IExecutionStackEntry> preexecute)
	{
		this.preexecute = preexecute;
	}

	/**
	 * Sets the init callback, which will be called first time before execution
	 * of stack entry. If this returns false, then stack entry will not be
	 * executed and will be skipped.
	 *
	 * @param onInit
	 *            the new init callback, which will be called first time before
	 *            execution of stack entry
	 */
	public void setOnInit(Function<IExecutionStackEntry, Boolean> onInit)
	{
		this.onInit = onInit;
	}
	
	public boolean initaize()
	{
		if(onInit == null)
		{
			return true;
		}
		
		return onInit.apply(this);
	}
	
	public void setOnComplete(Consumer<IExecutionStackEntry> onComplete)
	{
		this.onComplete = onComplete;
	}
	
	public Consumer<IExecutionStackEntry> getOnComplete()
	{
		return onComplete;
	}
	
	public void setExceptionHandler(IExceptionHandler exceptionHandler)
	{
		this.exceptionHandler = exceptionHandler;
	}
	
	public IExceptionHandler getExceptionHandler()
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
	
	@Override
	public IExecutionStackEntry setVariable(String name, Object value)
	{
		if(this.stackVariables == null)
		{
			this.stackVariables = new HashMap<String, Object>();
		}
		
		this.stackVariables.put(name, value);
		return this;
	}
	
	@Override
	public Object getVariable(String name)
	{
		return null;
	}
	
	public void stepStarted(IStep step)
	{
		if(stepListener == null)
		{
			return;
		}
		
		stepListener.stepStarted(step);
	}
	
	public void stepPhase(IStep step, String mssg)
	{
		if(stepListener == null)
		{
			return;
		}

		stepListener.stepPhase(step, mssg);
	}

	public void stepCompleted(IStep step, boolean successful, Exception ex)
	{
		if(stepListener == null)
		{
			return;
		}
		
		if(successful)
		{
			stepListener.stepCompleted(step);
		}
		else
		{
			stepListener.stepErrored(step, ex);
		}
	}

	public void completed(boolean successful)
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
	
	public void setReexecutionNeeded(Function<IExecutionStackEntry, Boolean> reexecutionNeeded)
	{
		this.reexecutionNeeded = reexecutionNeeded;
	}
	
	public boolean isReexecutionNeeded()
	{
		if(reexecutionNeeded == null)
		{
			return false;
		}
		
		return reexecutionNeeded.apply(this);
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
