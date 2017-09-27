package com.yukthitech.autox.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.ErrorDetails;
import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Utility methods for steps execution.
 * @author akiran
 */
public class StepExecutor
{
	private static Logger logger = LogManager.getLogger(StepExecutor.class);
	
	/**
	 * Executes specified step/validation. In case of validations, ensures result is true, in not {@link TestCaseValidationFailedException} will 
	 * be thrown.
	 * @param context context to be used
	 * @param exeLogger logger to be used
	 * @param step step to be executed
	 */
	public static void executeStep(AutomationContext context, ExecutionLogger exeLogger, IStep step) throws Exception
	{
		//if step is marked not to log anything
		if(step.isLoggingDisabled())
		{
			//disable logging
			exeLogger.setDisabled(true);
		}

		try
		{
			//clone the step, so that expression replacement will not affect actual step
			step = step.clone();
			
			AutomationUtils.replaceExpressions(context, step);
			boolean res = step.execute(context, exeLogger);
			
			if(step instanceof IValidation)
			{
				if(!res)
				{
					Executable executable = step.getClass().getAnnotation(Executable.class);
					exeLogger.error("Validation failed: " + executable.name() + step);
	
					throw new TestCaseValidationFailedException("Validation failed: " + executable.name());
				}
			}
		} finally
		{
			//re-enable logging, in case it is disabled
			exeLogger.setDisabled(false);
		}

	}
	
	private static void invokeErrorHandling(AutomationContext context, Executable executable, ErrorDetails errorDetails)
	{
		logger.debug("Invoking plugin error handling...");
		
		Class<? extends IPlugin<?>> pluginTypes[] = executable.requiredPluginTypes();
		
		if(pluginTypes == null || pluginTypes.length == 0)
		{
			return;
		}
		
		IPlugin<?> plugin = null;
		
		for(Class<? extends IPlugin<?>> type : pluginTypes)
		{
			plugin = context.getPlugin(type);
			
			if(plugin == null)
			{
				continue;
			}
			
			logger.debug("Invoking error handling of plugin - {}", type.getName());
			plugin.handleError(context, errorDetails);
		}
	}
	
	/**
	 * Creates executable proxy annotation for specified step-group.
	 * @param stepGroup
	 * @return
	 */
	private static Executable createExecutable(final StepGroup stepGroup)
	{
		Executable executable = (Executable) Proxy.newProxyInstance(StepExecutor.class.getClassLoader(), new Class[] {Executable.class}, new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				String methodName = method.getName();
				
				if("name".equals(methodName) || "message".equals(methodName))
				{
					return "StepGroup-" + stepGroup.getName();
				}
				
				return null;
			}
		});

		return executable;
	}
	
	/**
	 * Expected to be invoked by test cases, to process the exception and get appropriate result.
	 * @param context automation context
	 * @param testCase Test case being tested
	 * @param step Step which resulted in exception
	 * @param exeLogger logger to be used
	 * @param ex exception to be handled
	 * @param expectedException If test case is expecting exception, those details
	 * @return result based on input exception
	 */
	public static TestCaseResult handleException(AutomationContext context, TestCase testCase, IStep step, ExecutionLogger exeLogger, Exception ex, ExpectedException expectedException)
	{
		Executable executable = (step instanceof StepGroup) ?  createExecutable((StepGroup) step) : step.getClass().getAnnotation(Executable.class);
		String name = executable.name();
		
		String stepType = (step instanceof IValidation) ? "Validation" : "Step";
		
		if(ex instanceof TestCaseValidationFailedException)
		{
			invokeErrorHandling(context, executable, new ErrorDetails(exeLogger, testCase, step, ex));
			return new TestCaseResult(testCase.getName(), TestStatus.FAILED, exeLogger.getExecutionLogData(), ex.getMessage());
		}
		
		if(ex instanceof TestCaseFailedException)
		{
			invokeErrorHandling(context, executable, new ErrorDetails(exeLogger, testCase, step, ex));
			return new TestCaseResult(testCase.getName(), TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Validation errored: " + name);
		}
		
		if(expectedException != null)
		{
			try
			{
				expectedException.validateMatch(ex);
				
				exeLogger.debug("Expected excpetion occurred: {}", ex);
				return null;
			}catch(InvalidArgumentException iex)
			{
				exeLogger.error(ex, ex.getMessage());
				invokeErrorHandling(context, executable, new ErrorDetails(exeLogger, testCase, step, ex));
				return new TestCaseResult(testCase.getName(), TestStatus.ERRORED, exeLogger.getExecutionLogData(), stepType + " errored: " + name);
			}
		}

		//for unhandled exceptions log on ui
		exeLogger.error(ex, "An error occurred while executing " + stepType + ": " + name);
		invokeErrorHandling(context, executable, new ErrorDetails(exeLogger, testCase, step, ex));
		
		return new TestCaseResult(testCase.getName(), TestStatus.ERRORED, exeLogger.getExecutionLogData(), stepType + " errored: " + executable.name());
	}
}
