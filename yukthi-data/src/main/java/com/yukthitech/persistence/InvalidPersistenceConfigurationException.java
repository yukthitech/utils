package com.yukthitech.persistence;

public class InvalidPersistenceConfigurationException extends PersistenceException
{
	private static final long serialVersionUID = 1L;

	public InvalidPersistenceConfigurationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidPersistenceConfigurationException(String message)
	{
		super(message);
	}
}
