package com.yukthitech.autox.test.lang.steps;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.test.StepExecutor;

/**
 * Blocks of steps/validations that can be executed on demand.
 * @author akiran
 */
public class StepGroup implements IStepContainer
{
	/**
	 * Steps to be executed.
	 */
	private List<IStep> steps = new ArrayList<>();

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStepContainer#addStep(com.yukthitech.autox.IStep)
	 */
	@Override
	public void addStep(IStep step)
	{
		steps.add(step);
	}

	/**
	 * Executes all the underlying steps.
	 * @param context context to be used
	 * @param exeLogger logger to be used
	 */
	public void execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		// execute the steps involved
		for(IStep step : steps)
		{
			try
			{
				StepExecutor.executeStep(context, exeLogger, step);
			} catch(Exception ex)
			{
				if(ex instanceof LangException)
				{
					throw ex;
				}
				
				Executable executable = step.getClass().getAnnotation(Executable.class);
				exeLogger.error("An error occurred while executing child-step '{}'. Error: {}", executable.name(), ex);
				throw ex;
			}
		}
	}
}
