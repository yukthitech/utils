package com.yukthitech.autox.exec;

/**
 * Thrown by encapsulating actual exception, to indicate the 
 * actual exception is already handled.
 * 
 * @author akranthikiran
 */
public class HandledException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public HandledException(Throwable cause)
	{
		super(cause);
	}
}
