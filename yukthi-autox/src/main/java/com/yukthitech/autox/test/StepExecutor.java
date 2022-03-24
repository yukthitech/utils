package com.yukthitech.autox.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IValidation;
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
	
	private static void invokeErrorHandling(AutomationContext context, Executable executable, ErrorDetails errorDetails)
	{
		logger.debug( "Invoking plugin error handling for executable: {}", executable.name() );
		
		Collection< IPlugin<?> > pluginTypes = context.getPlugins();
		
		if(pluginTypes == null || pluginTypes.isEmpty())
		{
			logger.debug( "No associated plugins found in current context.");
			return;
		}
		
		for(IPlugin<?>  plugin : pluginTypes)
		{
			if(plugin == null)
			{
				continue;
			}
			
			logger.debug("Invoking error handling of plugin - {}", plugin.getClass().getName());
			
			try
			{
				plugin.handleError(context, errorDetails);
			}catch(Exception ex)
			{
				logger.error("An error occurred during plugin-error-handling with plugin: {}", plugin, ex);
			}
		}
	}
	
	/**
	 * Creates executable proxy annotation for specified step-group.
	 * @param function
	 * @return
	 */
	private static Executable createExecutable(final Function function)
	{
		Executable executable = (Executable) Proxy.newProxyInstance(StepExecutor.class.getClassLoader(), new Class[] {Executable.class}, new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				String methodName = method.getName();
				
				if("name".equals(methodName) || "message".equals(methodName))
				{
					return "Function-" + function.getName();
				}
				
				return null;
			}
		});

		return executable;
	}
	
	public static TestCaseResult handleException(AutomationContext context, TestCase testCase, IStep step, 
			ExecutionLogger exeLogger, Exception ex, ExpectedException expectedException, Date startTime)
	{
		return handleException(context, testCase, testCase.getName(), step, exeLogger, ex, expectedException, startTime);
	}
	
	/**
	 * Expected to be invoked by test cases, to process the exception and get appropriate result.
	 * @param context automation context
	 * @param testCase Test case being tested
	 * @param resName name to be used in result (useful for test case with data)
	 * @param step Step which resulted in exception
	 * @param exeLogger logger to be used
	 * @param ex exception to be handled
	 * @param expectedException If test case is expecting exception, those details
	 * @return result based on input exception
	 */
	public static TestCaseResult handleException(AutomationContext context, TestCase testCase, String resName, 
			IStep step, ExecutionLogger exeLogger, Exception ex, ExpectedException expectedException, Date startTime)
	{
		//from exception, try to find the step which caused the problem
		//	so that approp plugin handlers can be called.
		if(ex instanceof AutoxException)
		{
			AutoxException autoxException = (AutoxException) ex;
			IStep srcStep = autoxException.getSourceStep();
			
			if(srcStep != null)
			{
				logger.info("As the exception '{}' is caused by step '{}', considerting this step instead of input step: {}", ex, srcStep, step);
				step = srcStep;
			}
		}
		
		Executable executable = (step instanceof Function) ?  createExecutable((Function) step) : step.getClass().getAnnotation(Executable.class);
		String name = executable.name();
		
		String stepType = (step instanceof IValidation) ? "Validation" : "Step";
		
		if(ex instanceof TestCaseValidationFailedException)
		{
			invokeErrorHandling(context, executable, new ErrorDetails(exeLogger, testCase, step, ex));
			return new TestCaseResult(testCase, resName, TestStatus.FAILED, exeLogger.getExecutionLogData(), ex.getMessage(),
					startTime, new Date());
		}
		
		if(ex instanceof TestCaseFailedException)
		{
			invokeErrorHandling(context, executable, new ErrorDetails(exeLogger, testCase, step, ex));
			return new TestCaseResult(testCase, resName, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Validation errored: " + name,
					startTime, new Date());
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
				return new TestCaseResult(testCase, resName, TestStatus.ERRORED, exeLogger.getExecutionLogData(), stepType + " errored: " + name,
						startTime, new Date());
			}
		}

		//for unhandled exceptions log on ui
		exeLogger.error(ex, "An error occurred while executing " + stepType + ": " + name);
		invokeErrorHandling(context, executable, new ErrorDetails(exeLogger, testCase, step, ex));
		
		return new TestCaseResult(testCase, resName, TestStatus.ERRORED, exeLogger.getExecutionLogData(), stepType + " errored: " + executable.name(),
				startTime, new Date());
	}
}
