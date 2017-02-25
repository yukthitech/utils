package com.yukthitech.utils.expr;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Thrown when invalid parameter type is encountered in an expression.
 * @author akiran
 */
public class InvalidTypeException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid type exception.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public InvalidTypeException(String message, Object... args)
	{
		super(message, args);
	}

	/**
	 * Instantiates a new invalid type exception.
	 *
	 * @param cause the cause
	 * @param message the message
	 * @param args the args
	 */
	public InvalidTypeException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}
