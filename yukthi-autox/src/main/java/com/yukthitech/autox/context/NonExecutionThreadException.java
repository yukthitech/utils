package com.yukthitech.autox.context;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Thrown when non-execution thread is invoking execution specific operations.
 * @author akranthikiran
 */
public class NonExecutionThreadException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	public NonExecutionThreadException(String message, Object... args)
	{
		super(message, args);
	}
}
