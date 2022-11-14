package com.yukthitech.autox.config;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Represent error details that needs to be sent to plugin for error handling.
 * @author akiran
 */
public class ErrorDetails
{
	/**
	 * Logger for logging messages.
	 */
	private IExecutionLogger executionLogger;
	
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
	 * @param step the step
	 * @param exception the exception
	 */
	public ErrorDetails(IExecutionLogger executionLogger, IStep step, Exception exception)
	{
		this.executionLogger = executionLogger;
		this.step = step;
		this.exception = exception;
	}

	/**
	 * Gets the logger for logging messages.
	 *
	 * @return the logger for logging messages
	 */
	public IExecutionLogger getExecutionLogger()
	{
		return executionLogger;
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
