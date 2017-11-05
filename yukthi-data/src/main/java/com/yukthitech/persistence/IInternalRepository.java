package com.yukthitech.persistence;

import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.persistence.repository.executors.QueryExecutor;

/**
 * Specifies repository methods meant for internal use by fw-data
 * @author akiran
 */
public interface IInternalRepository
{
	/**
	 * Drops corresponding entity table 
	 */
	public void dropEntityTable();
	
	/**
	 * Gets the actual repository type of this instance
	 * @return
	 */
	public Class<?> getRepositoryType();

	/**
	 * Fetches parent repository factory.
	 * @return  parent repository factory.
	 */
	public RepositoryFactory getRepositoryFactory();
	
	/**
	 * Fetches the data store used by this repository.
	 * @return underlying data store.
	 */
	public IDataStore getDataStore();
	
	/**
	 * Executes the specified query executor with specified params and returns the result.
	 * @param queryExecutor executor to execute
	 * @param params params to be passed for execution
	 * @return query executor result
	 */
	public Object executeQueryExecutor(QueryExecutor queryExecutor, Object... params);
}
