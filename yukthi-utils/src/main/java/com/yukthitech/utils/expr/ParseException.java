package com.yukthitech.utils.expr;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Exception to be thrown when expression parsing resulted in error.
 * @author akiran
 */
public class ParseException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new parses the exception.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public ParseException(String message, Object... args)
	{
		super(message, args);
	}

	/**
	 * Instantiates a new parses the exception.
	 *
	 * @param cause the cause
	 * @param message the message
	 * @param args the args
	 */
	public ParseException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}
