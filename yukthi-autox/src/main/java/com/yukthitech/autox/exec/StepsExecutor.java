package com.yukthitech.autox.exec;

import java.util.List;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.test.TestCaseValidationFailedException;
import com.yukthitech.autox.test.lang.steps.LangException;
import com.yukthitech.utils.ObjectWrapper;

/**
 * Executor for steps.
 * @author akranthikiran
 */
public class StepsExecutor
{
	private static ThreadLocal<Boolean> topLevelStep = new ThreadLocal<>();
	
	private static boolean checkForTopLevel()
	{
		Boolean val = topLevelStep.get();
		
		if(val == null)
		{
			topLevelStep.set(Boolean.TRUE);
			return true;
		}
		
		return false;
	}
	
	private static void clearTopLevel()
	{
		topLevelStep.remove();
	}
	
	/**
	 * Executes specified step/validation. In case of validations, ensures result is true, in not {@link TestCaseValidationFailedException} will 
	 * be thrown.
	 * @param context context to be used
	 * @param exeLogger logger to be used
	 * @param step step to be executed
	 */
	private static void executeStep(ExecutionLogger exeLogger, IStep step) throws Exception
	{
		//if step is marked not to log anything
		if(step.isLoggingDisabled())
		{
			//disable logging
			exeLogger.setDisabled(true);
		}

		AutomationContext context = AutomationContext.getInstance();
		context.getExecutionStack().push(step);
		boolean isTopLevel = checkForTopLevel();
		
		try
		{
			context.setExecutionLogger(exeLogger);
			
			//clone the step, so that expression replacement will not affect actual step
			step = step.clone();
			AutomationUtils.replaceExpressions("step-" + step.getClass().getName(), context, step);

			//context.getStepListenerProxy().stepStarted(step, null);
			step.execute(context, exeLogger);
			//context.getStepListenerProxy().stepCompleted(step, currentData);
		} catch(RuntimeException ex)
		{
			//context.getStepListenerProxy().stepErrored(step, currentData, ex);
			
			if(ex instanceof LangException)
			{
				throw ex;
			}
			
			//only top level step in stack should log the error
			if(isTopLevel)
			{
				exeLogger.error("An error occurred with message - {}. Stack Trace: {}", ex.getMessage(), context.getExecutionStack().toStackTrace());
			}
			
			throw ex;
		}finally
		{
			if(isTopLevel)
			{
				clearTopLevel();
			}
			
			context.getExecutionStack().pop(step);
			
			//re-enable logging, in case it is disabled
			exeLogger.setDisabled(false);
			context.setExecutionLogger(null);
		}

	}
	
	public static void execute(ExecutionLogger logger, List<IStep> steps, ObjectWrapper<IStep> currentStep) throws Exception
	{
		for(IStep step : steps)
		{
			if(currentStep != null)
			{
				currentStep.setValue(step);
			}
			
			executeStep(logger, step);
		}
	}
}
