package com.yukthitech.autox.exec;

import java.util.List;

/**
 * Base interface for executors.
 * @author akranthikiran
 */
public interface IExecutor
{
	/**
	 * This will be used to generated report file name.
	 * @return
	 */
	public String getName();
	
	public String getCode();
	
	public String getDescription();
	
	public default List<IExecutor> getDependencies()
	{
		return null;
	}
	
	/**
	 * Invoked before execution. If this method returns null execution occurs immediately.
	 * If other executor is returned, then this method will be invoked again post execution
	 * of specified executor.
	 *  
	 * @return Dependency executor for which executor is waiting for. Null if this executor is ready
	 * to execute.
	 */
	public default boolean isReadyToExecute()
	{
		return true;
	}

	/**
	 * This is invoked once all dependencies (if any) are completed and this executor is ready to execute.
	 * In this method data-provider execution (and child population) should occur.
	 */
	public default void init()
	{}
	
	/**
	 * Actual execution logic goes here.
	 */
	public void execute();
}
