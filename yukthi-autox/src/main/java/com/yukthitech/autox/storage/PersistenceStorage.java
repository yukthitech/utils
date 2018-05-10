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
	
	/**
	 * Execution info repository.
	 */
	private IExecutionInfoRepository executionInfoRepository;
	
	public PersistenceStorage(ApplicationConfiguration config)
	{
		if(config.getStorageRepositoryFactory() != null)
		{
			this.dataTableRepository = config.getStorageRepositoryFactory().getRepository(IDataTableRepository.class);
			this.executionInfoRepository = config.getStorageRepositoryFactory().getRepository(IExecutionInfoRepository.class);
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
	
	/**
	 * Deletes entry with specified key.
	 * @param key key to be delete.
	 * @return true if deleted.
	 */
	public boolean remove(String key)
	{
		if(dataTableRepository == null)
		{
			throw new IllegalStateException("No storage repository specified for storage.");
		}
		
		return dataTableRepository.deleteByKey(key);
	}
	
	/**
	 * To be called before starting test case execution. This will store execution details in storage.
	 * @param testSuite Test suite being executed.
	 * @param testCase test case being executed.
	 * @return Id of the execution, which can be used to update result
	 */
	public long testCaseStarted(String testSuite, String testCase)
	{
		if(executionInfoRepository == null)
		{
			return -1;
		}
		
		ExecutionInfoEntity executionInfoEntity = new ExecutionInfoEntity(testSuite, testCase);
		boolean res = executionInfoRepository.save(executionInfoEntity);
		
		if(!res)
		{
			throw new IllegalStateException("Failed to persist execution details, please check log for more details.");
		}
		
		return executionInfoEntity.getId();
	}
	
	/**
	 * Updates the execution with specified result info.
	 * @param id id of execution to update
	 * @param successful flag indicating if execution was successful.
	 * @param timeTaken time taken for execution.
	 * @param errorMessage Error message if any.
	 */
	public void updateExecution(long id, boolean successful, long timeTaken, String errorMessage)
	{
		if(executionInfoRepository == null)
		{
			return;
		}
		
		boolean res = executionInfoRepository.updateResult(id, successful, timeTaken, errorMessage);

		if(!res)
		{
			throw new IllegalStateException("Failed to update execution details, please check log for more details.");
		}
	}
}
