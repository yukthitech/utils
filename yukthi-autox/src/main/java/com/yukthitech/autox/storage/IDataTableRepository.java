package com.yukthitech.autox.storage;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.AggregateFunction;
import com.yukthitech.persistence.repository.annotations.AggregateFunctionType;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;

/**
 * Repository for data table.
 * @author akiran
 */
public interface IDataTableRepository extends ICrudRepository<DataTableEntity>
{
	/**
	 * Checks if the specified key already exists or not.
	 * @param key
	 * @return
	 */
	@AggregateFunction(type = AggregateFunctionType.COUNT)
	public boolean isExistingKey(@Condition("key") String key);
	
	/**
	 * Updates value for specified key with specified value.
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean updateValue(@Condition("key") String key, @Field("value") Object value);
	
	/**
	 * Deletes the entry with specified key.
	 * @param key key to be deleted
	 * @return true if deleted.
	 */
	public boolean deleteByKey(@Condition("key") String key);
	
	/**
	 * Fetches value with specified key.
	 * @param key
	 * @return
	 */
	@Field("value")
	public Object fetchByKey(String key);
}
