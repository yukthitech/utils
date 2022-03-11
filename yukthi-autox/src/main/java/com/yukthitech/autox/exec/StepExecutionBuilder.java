package com.yukthitech.autox.exec;

import java.util.List;
import java.util.function.Consumer;

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
	
	private Consumer<ExecutionStackEntry> onSuccess;
	
	private Consumer<ExecutionStackEntry> onInit;
	
	private Consumer<ExecutionStackEntry> onComplete;
	
	private ExceptionHandler exceptionHandler;
	

	StepExecutionBuilder(AutomationExecutor parentExecutor, String label, Object executable, List<IStep> steps)
	{
		this.parentExecutor = parentExecutor;
		
		this.stackEntry = new ExecutionStackEntry(label, executable, steps);
	}
	
	public StepExecutionBuilder onSuccess(Consumer<ExecutionStackEntry> onSuccess)
	{
		this.onSuccess = onSuccess;
		return this;
	}
	
	public StepExecutionBuilder onInit(Consumer<ExecutionStackEntry> onInit)
	{
		this.onInit = onInit;
		return this;
	}

	public StepExecutionBuilder onComplete(Consumer<ExecutionStackEntry> onComplete)
	{
		this.onComplete = onComplete;
		return this;
	}

	public StepExecutionBuilder exceptionHandler(ExceptionHandler exceptionHandler)
	{
		this.exceptionHandler = exceptionHandler;
		return this;
	}
	
	public void push()
	{
		if(CollectionUtils.isEmpty(stackEntry.getSteps()))
		{
			return;
		}
		
		stackEntry.setOnSuccess(onSuccess);
		stackEntry.setOnInit(onInit);
		stackEntry.setOnComplete(onComplete);
		stackEntry.setExceptionHandler(exceptionHandler);
		
		parentExecutor.pushSteps(stackEntry);

	}
}
