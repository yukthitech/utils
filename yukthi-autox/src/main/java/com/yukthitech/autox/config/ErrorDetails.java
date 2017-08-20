package com.yukthitech.autox.config;

import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.test.TestCase;

/**
 * Represent error details that needs to be sent to plugin for error handling.
 * @author akiran
 */
public class ErrorDetails
{
	/**
	 * Logger for logging messages.
	 */
	private ExecutionLogger executionLogger;
	
	/**
	 * Test case which resulted in error.
	 */
	private TestCase testCase;
	
	/**
	 * Step resulted in error.
	 */
	private IStep step;
	
	/**
	 * Error occurred during test case execution.
	 */
	private Exception exception;

	/**
	 * Instantiates a new error details.
	 *
	 * @param executionLogger the execution logger
	 * @param testCase the test case
	 * @param step the step
	 * @param exception the exception
	 */
	public ErrorDetails(ExecutionLogger executionLogger, TestCase testCase, IStep step, Exception exception)
	{
		this.executionLogger = executionLogger;
		this.testCase = testCase;
		this.step = step;
		this.exception = exception;
	}

	/**
	 * Gets the logger for logging messages.
	 *
	 * @return the logger for logging messages
	 */
	public ExecutionLogger getExecutionLogger()
	{
		return executionLogger;
	}

	/**
	 * Gets the test case which resulted in error.
	 *
	 * @return the test case which resulted in error
	 */
	public TestCase getTestCase()
	{
		return testCase;
	}

	/**
	 * Gets the step resulted in error.
	 *
	 * @return the step resulted in error
	 */
	public IStep getStep()
	{
		return step;
	}

	/**
	 * Gets the error occurred during test case execution.
	 *
	 * @return the error occurred during test case execution
	 */
	public Exception getException()
	{
		return exception;
	}
}
