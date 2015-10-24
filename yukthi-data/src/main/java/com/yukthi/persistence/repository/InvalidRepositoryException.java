package com.yukthi.persistence.repository;

import com.yukthi.persistence.PersistenceException;

public class InvalidRepositoryException extends PersistenceException
{
	private static final long serialVersionUID = 1L;

	public InvalidRepositoryException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidRepositoryException(String message)
	{
		super(message);
	}
}
