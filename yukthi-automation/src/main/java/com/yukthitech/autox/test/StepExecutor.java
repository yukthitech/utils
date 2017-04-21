package com.yukthitech.autox.test;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Utility methods for steps execution.
 * @author akiran
 */
public class StepExecutor
{
	/**
	 * Executes specified step/validation. In case of validations, ensures result is true, in not {@link TestCaseValidationFailedException} will 
	 * be thrown.
	 * @param context context to be used
	 * @param exeLogger logger to be used
	 * @param step step to be executed
	 */
	public static void executeStep(AutomationContext context, ExecutionLogger exeLogger, IStep step) throws Exception
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
	}
	
	/**
	 * Expected to be invoked by test cases, to process the exception and get appropriate result.
	 * @param step Step which resulted in exception
	 * @param exeLogger logger to be used
	 * @param ex exception to be handled
	 * @param expectedException If test case is expecting exception, those details
	 * @return result based on input exception
	 */
	public static TestCaseResult handleException(IStep step, ExecutionLogger exeLogger, Exception ex, ExpectedException expectedException)
	{
		Executable executable = step.getClass().getAnnotation(Executable.class);
		String name = executable.name();
		
		String stepType = (step instanceof IValidation) ? "Validation" : "Step";
		
		if(ex instanceof TestCaseValidationFailedException)
		{
			return new TestCaseResult(name, TestStatus.FAILED, exeLogger.getExecutionLogData(), ex.getMessage());
		}
		
		if(ex instanceof TestCaseFailedException)
		{
			return new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Validation errored: " + name);
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
				return new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), stepType + " errored: " + name);
			}
		}

		//for unhandled exceptions log on ui
		exeLogger.error(ex, "An error occurred while executing validation: " + name);
		
		return new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), stepType + " errored: " + executable.name());
	}
}
