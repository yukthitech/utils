package com.yukthitech.autox.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

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
			context.setExecutionLogger(exeLogger);
			
			boolean res = true;
			
			if(step.getDataProvider() == null)
			{
				//clone the step, so that expression replacement will not affect actual step
				step = step.clone();
				AutomationUtils.replaceExpressions("step-" + step.getClass().getName(), context, step);

				res = step.execute(context, exeLogger);
			}
			else
			{
				exeLogger.debug(step, "Executing the step with data provider.");
				IDataProvider dataProvider = step.getDataProvider();
				List<TestCaseData> dataLst = dataProvider.getStepData();
				
				if(dataLst == null)
				{
					exeLogger.debug(step, "Data provider resulted in empty data list.");
				}
				else
				{
					for(TestCaseData data : dataLst)
					{
						if(dataProvider.isParsingEnabled())
						{
							AutomationUtils.replaceExpressions("testCaseData", context, data);
						}
						
						exeLogger.debug(step, "Executing the step with [Data provider: {}, Data: {}]", dataProvider.getName(), data.getName());
						context.setAttribute(dataProvider.getName(), data.getValue());
						
						//clone the step, so that expression replacement will not affect actual step
						IStep dataStep = step.clone();
						AutomationUtils.replaceExpressions("step-" + dataStep.getClass().getName(), context, dataStep);

						if(!dataStep.execute(context, exeLogger))
						{
							res = false;
						}
					}
				}
			}
			
			if(step instanceof IValidation)
			{
				if(!res)
				{
					Executable executable = step.getClass().getAnnotation(Executable.class);
					
					String message = String.format("Validation %s failed. Validation Details: %s", executable.name()[0], step);
					
					exeLogger.error(step, message);
					throw new TestCaseValidationFailedException(step, message);
				}
			}
		} finally
		{
			//re-enable logging, in case it is disabled
			exeLogger.setDisabled(false);
			context.setExecutionLogger(null);
		}

	}
	
	private static void invokeErrorHandling(AutomationContext context, Executable executable, ErrorDetails errorDetails)
	{
		logger.debug( "Invoking plugin error handling for executable: {}", Arrays.toString(executable.name()) );
		
		Class<? extends IPlugin<?>> pluginTypes[] = executable.requiredPluginTypes();
		
		if(pluginTypes == null || pluginTypes.length == 0)
		{
			logger.debug( "No associated plugins gound for executable: {}", Arrays.toString(executable.name()) );
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
		String name = executable.name()[0];
		
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
				
				exeLogger.debug(step, "Expected excpetion occurred: {}", ex);
				return null;
			}catch(InvalidArgumentException iex)
			{
				exeLogger.error(step, ex, ex.getMessage());
				invokeErrorHandling(context, executable, new ErrorDetails(exeLogger, testCase, step, ex));
				return new TestCaseResult(testCase.getName(), TestStatus.ERRORED, exeLogger.getExecutionLogData(), stepType + " errored: " + name);
			}
		}

		//for unhandled exceptions log on ui
		exeLogger.error(step, ex, "An error occurred while executing " + stepType + ": " + name);
		invokeErrorHandling(context, executable, new ErrorDetails(exeLogger, testCase, step, ex));
		
		return new TestCaseResult(testCase.getName(), TestStatus.ERRORED, exeLogger.getExecutionLogData(), stepType + " errored: " + executable.name());
	}
}
