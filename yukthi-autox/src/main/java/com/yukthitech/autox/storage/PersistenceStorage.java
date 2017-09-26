package com.yukthitech.autox.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.config.ApplicationConfiguration;

/**
 * Persistence storage used to store automation related data.
 * @author akiran
 */
public class PersistenceStorage
{
	private static Logger logger = LogManager.getLogger(PersistenceStorage.class);
	
	/**
	 * Data table repository.
	 */
	private IDataTableRepository dataTableRepository;
	
	public PersistenceStorage(ApplicationConfiguration config)
	{
		if(config.getStorageRepositoryFactory() != null)
		{
			this.dataTableRepository = config.getStorageRepositoryFactory().getRepository(IDataTableRepository.class);
		}
	}
	
	/**
	 * Sets the specified key-value into persistence storage.
	 * @param key Key to be used
	 * @param value value to set
	 */
	public void set(String key, Object value)
	{
		if(dataTableRepository == null)
		{
			throw new IllegalStateException("No storage repository specified for storage.");
		}
		
		boolean existingKey = dataTableRepository.isExistingKey(key);
		
		if(existingKey)
		{
			logger.debug("Updating entry with key: {}", key);
			dataTableRepository.updateValue(key, value);
		}
		else
		{
			logger.debug("Inserting new entry with key: {}", key);
			dataTableRepository.save(new DataTableEntity(key, value));
		}
	}
	
	/**
	 * Used to fetch value for specified key.
	 * @param key
	 * @return
	 */
	public Object get(String key)
	{
		if(dataTableRepository == null)
		{
			throw new IllegalStateException("No storage repository specified for storage.");
		}
		
		return dataTableRepository.fetchByKey(key);
	}
}
