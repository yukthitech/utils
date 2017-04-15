package com.yukthitech.autox.test;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

public class StepExecutor
{
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
	
	public static TestCaseResult handleException(IStep step, ExecutionLogger exeLogger, Exception ex, ExpectedException expectedException)
	{
		Executable executable = step.getClass().getAnnotation(Executable.class);
		String name = executable.name();
		
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
				return new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Step errored: " + name);
			}
		}

		//for unhandled exceptions log on ui
		exeLogger.error(ex, "An error occurred while executing validation: " + executable.name());
		
		return new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Validation errored: " + executable.name());
	}
}
