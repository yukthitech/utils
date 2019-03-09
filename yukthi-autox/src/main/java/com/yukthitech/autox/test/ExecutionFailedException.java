package com.yukthitech.autox.test;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Used when setup/cleanup execution is failed.
 * @author akiran
 */
public class ExecutionFailedException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	public ExecutionFailedException(String message, Object... args)
	{
		super(message, args);
	}

	public ExecutionFailedException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}
