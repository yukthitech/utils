package com.yukthitech.persistence;

/**
 * Thrown when invalid mapping is encountered.
 * @author akiran
 */
public class InvalidMappingException extends PersistenceException
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid mapping exception.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public InvalidMappingException(String message, Object... args)
	{
		super(message, args);
	}

	/**
	 * Instantiates a new invalid mapping exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidMappingException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new invalid mapping exception.
	 *
	 * @param message the message
	 */
	public InvalidMappingException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new invalid mapping exception.
	 *
	 * @param cause the cause
	 * @param message the message
	 * @param args the args
	 */
	public InvalidMappingException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}
