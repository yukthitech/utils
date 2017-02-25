package com.yukthitech.persistence;

import java.util.List;
import java.util.Set;

import com.yukthitech.persistence.repository.annotations.AggregateFunction;
import com.yukthitech.persistence.repository.annotations.ExtendedFieldNames;
import com.yukthitech.persistence.repository.search.SearchQuery;

public interface ICrudRepository<E>
{
	/**
	 * Sets the execution context for this repository. This object will be used to fetch context parameters.
	 * This context can return values based on thread, which would be useful for webapplications.
	 * @param context Context to be set
	 */
	public void setExecutionContext(Object context);
	
	public EntityDetails getEntityDetails();

	/**
	 * Creates a new transaction and registers for current thread. If a transaction is already in progress
	 * by current thread, this method will throw exception. 
	 * @return New transaction object
	 */
	public ITransaction newTransaction();
	
	/**
	 * Obtains currently running transaction registered with current thread. If no transaction is present or started
	 * by current thread, this method throws exception. 
	 * 
	 * Commits or close will not have effect on the transaction returned.
	 * 
	 * @return Currently running transaction
	 */
	public ITransaction currentTransaction();
	
	/**
	 * Checks if current thread already started transaction, if found same will be returned, if not creates new transaction
	 * and returns the same.
	 * 
	 * Commits or close will not have effect if existing transaction is used, so that final commit can be done on first transaction.
	 * 
	 * @return Existing transaction, if any, if not new transaction
	 */
	public ITransaction newOrExistingTransaction();
	
	/**
	 * Saves the entity to underlying store
	 * @param entity
	 */
	public boolean save(E entity);

	public boolean update(E entity);
	
	public boolean deleteById(Object key);
	
	public E findById(Object key);
	
	/**
	 * Fetches the entity with specified id, with specified custom fields.
	 * @param key Id of the entity to fetch
	 * @param customFieldNames Custom fields to fetch with entities
	 * @return Entity with custom field values.
	 */
	public E findFullById(Object key, @ExtendedFieldNames Set<String> customFieldNames);
	
	/**
	 * Fetches the count of number of entities in this repository 
	 * @return
	 */
	@AggregateFunction
	public long getCount();
	
	/**
	 * Common search method to support dynamic search functionality by default
	 * @param searchQuery
	 * @return
	 */
	public List<E> search(SearchQuery searchQuery);
	
	/**
	 * Fetches number of matching records for the specified search query.
	 * @param searchQuery Search query to match
	 * @return Number of matching records.
	 */
	public long searchCount(SearchQuery searchQuery);
}
