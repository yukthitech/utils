package com.yukthitech.autox.storage;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;

/**
 * Repository for execution info.
 * @author akiran
 */
public interface IExecutionInfoRepository extends ICrudRepository<ExecutionInfoEntity>
{
	/**
	 * Updates the execution with specified result info.
	 * @param id id of execution to update
	 * @param successful flag indicating if execution was successful.
	 * @param timeTaken time taken for execution.
	 * @param errorMessage Error message if any.
	 * @return Returns if successful or not
	 */
	public boolean updateResult(@Condition("id") long id, @Field("successful") boolean successful, @Field("timeTaken") long timeTaken, @Field("errorMessage") String errorMessage);
}
