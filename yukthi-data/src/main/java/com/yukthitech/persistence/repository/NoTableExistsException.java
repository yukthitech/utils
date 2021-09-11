package com.yukthitech.persistence.repository;

import com.yukthitech.persistence.PersistenceException;

/**
 * Exception that will be thrown when table does not exists.
 * @author akiran
 */
public class NoTableExistsException extends PersistenceException
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of missing table.
	 */
	private String tableName;

	public NoTableExistsException(Class<?> repositoryType, String tableName)
	{
		super("Repository {} is configured with non-existing table: {}", repositoryType.getName(), tableName);
		this.tableName = tableName;
	}
	
	/**
	 * Gets the name of missing table.
	 *
	 * @return the name of missing table
	 */
	public String getTableName()
	{
		return tableName;
	}
}
