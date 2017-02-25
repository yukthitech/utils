package com.yukthitech.utils.cli;

import com.yukthitech.utils.exceptions.UtilsCheckedException;

/**
 * Exception to be thrown when required argument is not specified.
 * @author akiran
 */
public class MissingArgumentException extends UtilsCheckedException
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new missing argument exception.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public MissingArgumentException(String message, Object... args)
	{
		super(message, args);
	}

	/**
	 * Instantiates a new missing argument exception.
	 *
	 * @param cause the cause
	 * @param message the message
	 * @param args the args
	 */
	public MissingArgumentException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}
