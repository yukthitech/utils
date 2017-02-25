package com.yukthitech.persistence;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Base exception for persistence layer exceptions.
 * @author akiran
 */
public class PersistenceException extends UtilsException
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new persistence exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public PersistenceException(String message, Throwable cause)
	{
		super(cause, message);
	}

	/**
	 * Instantiates a new persistence exception.
	 *
	 * @param message the message
	 */
	public PersistenceException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new persistence exception.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public PersistenceException(String message, Object... args)
	{
		super(message, args);
	}

	/**
	 * Instantiates a new persistence exception.
	 *
	 * @param cause the cause
	 * @param message the message
	 * @param args the args
	 */
	public PersistenceException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}
