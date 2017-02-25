package com.yukthitech.ccg.xml;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Exception thrown when data type incompatibility is encountered.
 * 
 * @author akiran
 */
public class UnsupportedDataTypeException extends UtilsException
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new unsupported data type exception.
	 *
	 * @param message
	 *            the message
	 * @param args
	 *            the args
	 */
	public UnsupportedDataTypeException(String message, Object... args)
	{
		super(message, args);
	}

	/**
	 * Instantiates a new unsupported data type exception.
	 *
	 * @param cause
	 *            the cause
	 * @param message
	 *            the message
	 * @param args
	 *            the args
	 */
	public UnsupportedDataTypeException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}
