package com.yukthitech.autox.test;

/**
 * To be thrown when setup execution is failed.
 * @author akiran
 */
public class SetupExecutionFailedException extends ExecutionFailedException
{
	private static final long serialVersionUID = 1L;

	public SetupExecutionFailedException(Throwable cause, String message, Object[] args)
	{
		super(cause, message, args);
	}

	public SetupExecutionFailedException(String message, Object... args)
	{
		super(message, args);
	}
}
