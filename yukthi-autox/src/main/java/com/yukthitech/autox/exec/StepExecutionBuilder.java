package com.yukthitech.autox.exec;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.IStep;

/**
 * Builder for step execution.
 * @author akranthikiran
 */
public class StepExecutionBuilder
{
	private AutomationExecutor parentExecutor;
	
	private ExecutionStackEntry stackEntry;
	
	private Consumer<IExecutionStackEntry> onSuccess;
	
	private Function<IExecutionStackEntry, Boolean> onInit;
	
	private Consumer<IExecutionStackEntry> preexecute;
	
	private Function<IExecutionStackEntry, Boolean> reexecutionNeeded;
	
	private Consumer<IExecutionStackEntry> onComplete;
	
	private IExceptionHandler exceptionHandler;
	

	StepExecutionBuilder(AutomationExecutor parentExecutor, String label, Object executable, List<IStep> steps, ExecutionType executionType)
	{
		this.parentExecutor = parentExecutor;
		
		this.stackEntry = new ExecutionStackEntry(label, executable, steps, executionType);
	}
	
	public StepExecutionBuilder preexecute(Consumer<IExecutionStackEntry> preexecute)
	{
		this.preexecute = preexecute;
		return this;
	}
	
	public StepExecutionBuilder onSuccess(Consumer<IExecutionStackEntry> onSuccess)
	{
		this.onSuccess = onSuccess;
		return this;
	}
	
	/**
	 * Init callback, which will be called first time before execution of stack entry. 
	 * If this returns false, then stack entry will not be executed and will be skipped.
	 * 
	 * @param onInit
	 * @return
	 */
	public StepExecutionBuilder onInit(Function<IExecutionStackEntry, Boolean> onInit)
	{
		this.onInit = onInit;
		return this;
	}

	/**
	 * Same as {@link #onInit}, in this method the return value is fixed to true.
	 * 
	 * @param onInit
	 * @return
	 */
	public StepExecutionBuilder onSimpleInit(Consumer<IExecutionStackEntry> onInit)
	{
		this.onInit = entry -> 
		{
			onInit.accept(entry);
			return true;
		};
		
		return this;
	}

	public StepExecutionBuilder onComplete(Consumer<IExecutionStackEntry> onComplete)
	{
		this.onComplete = onComplete;
		return this;
	}

	public StepExecutionBuilder exceptionHandler(IExceptionHandler exceptionHandler)
	{
		this.exceptionHandler = exceptionHandler;
		return this;
	}
	
	public StepExecutionBuilder isReexecutionNeeded(Function<IExecutionStackEntry, Boolean> reexecutionNeeded)
	{
		this.reexecutionNeeded = reexecutionNeeded;
		return this;
	}
	
	public void execute()
	{
		if(CollectionUtils.isEmpty(stackEntry.getSteps()))
		{
			return;
		}
		
		stackEntry.setOnSuccess(onSuccess);
		stackEntry.setOnInit(onInit);
		stackEntry.setPreexecute(preexecute);
		stackEntry.setOnComplete(onComplete);
		stackEntry.setExceptionHandler(exceptionHandler);
		stackEntry.setReexecutionNeeded(reexecutionNeeded);
		
		parentExecutor.pushSteps(stackEntry);

	}
}
