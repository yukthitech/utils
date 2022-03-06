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
	
	private String label;
	
	private List<IStep> steps;
	
	private Consumer<ExecutionStackEntry> onSuccess;
	
	private ExceptionHandler exceptionHandler;

	StepExecutionBuilder(AutomationExecutor parentExecutor, String label, List<IStep> steps)
	{
		this.parentExecutor = parentExecutor;
		this.label = label;
		this.steps = steps;
	}
	
	public StepExecutionBuilder onSuccess(Consumer<ExecutionStackEntry> onSuccess)
	{
		this.onSuccess = onSuccess;
		return this;
	}
	
	public StepExecutionBuilder exceptionHandler(ExceptionHandler exceptionHandler)
	{
		this.exceptionHandler = exceptionHandler;
		return this;
	}
	
	public void push()
	{
		if(CollectionUtils.isEmpty(steps))
		{
			return;
		}
		
		ExecutionStackEntry entry = new ExecutionStackEntry(label, steps);
		entry.setOnSuccess(onSuccess);
		entry.setExceptionHandler(exceptionHandler);
		
		parentExecutor.pushSteps(entry);

	}
}
