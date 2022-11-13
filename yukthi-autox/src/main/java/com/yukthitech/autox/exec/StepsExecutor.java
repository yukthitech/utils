package com.yukthitech.autox.exec;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.IExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepListener;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.test.TestCaseValidationFailedException;
import com.yukthitech.autox.test.lang.steps.LangException;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.event.EventListenerManager;

/**
 * Executor for steps.
 * @author akranthikiran
 */
public class StepsExecutor
{
	private static ThreadLocal<Boolean> topLevelStep = new ThreadLocal<>();
	
	private static EventListenerManager<IStepListener> stepListeners = EventListenerManager.newEventListenerManager(IStepListener.class, false);
	
	public static void addStepListener(IStepListener listener)
	{
		stepListeners.addListener(listener);
	}
	
	public static void removeStepListener(IStepListener listener)
	{
		stepListeners.removeListener(listener);
	}
	
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
	private static void executeStep(IExecutionLogger exeLogger, IStep step) throws Exception
	{
		//if step is marked not to log anything
		if(step.isLoggingDisabled())
		{
			//disable logging
			exeLogger.setDisabled(true);
		}

		AutomationContext context = AutomationContext.getInstance();
		context.setExecutionLogger(exeLogger);
		
		//clone the step, so that expression replacement will not affect actual step
		step = step.clone();

		context.getExecutionStack().push(step);
		boolean isTopLevel = checkForTopLevel();
		
		try
		{
			AutomationUtils.replaceExpressions("step-" + step.getClass().getName(), context, step);

			stepListeners.get().stepStarted(step);
			
			step.execute(context, exeLogger);
			
			stepListeners.get().stepCompleted(step);
		} catch(RuntimeException ex)
		{
			stepListeners.get().stepErrored(step, ex);
			
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
	
	public static void execute(IExecutionLogger logger, List<IStep> steps, ObjectWrapper<IStep> currentStep) throws Exception
	{
		if(CollectionUtils.isEmpty(steps))
		{
			return;
		}
		
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
