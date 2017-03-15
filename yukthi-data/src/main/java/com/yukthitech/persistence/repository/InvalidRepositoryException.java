package com.yukthitech.persistence.repository;

import com.yukthitech.persistence.PersistenceException;
import com.yukthitech.utils.MessageFormatter;

/**
 * Will be thrown when invalid configuration is found on repository
 * @author akiran
 */
public class InvalidRepositoryException extends PersistenceException
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid repository exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidRepositoryException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new invalid repository exception. With argumentation message support.
	 *
	 * @param cause the cause
	 * @param message the message with argument params
	 * @param args the arguments for message
	 */
	public InvalidRepositoryException(Throwable cause, String message, Object... args)
	{
		super(MessageFormatter.format(message, args), cause);
	}

	/**
	 * Instantiates a new invalid repository exception.
	 *
	 * @param message the message with argument params
	 * @param args the arguments for message
	 */
	public InvalidRepositoryException(String message, Object... args)
	{
		super(MessageFormatter.format(message, args));
	}
	
}
