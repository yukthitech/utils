package com.yukthitech.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Utility class used to execute tasks which throws exception, which generally are
 * not expected to be thrown.
 * @author akiran
 */
public class ExecutionUtils
{
	
	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(ExecutionUtils.class);
	
	/**
	 * Interface to represent tasks that can be submitted.
	 * @author akiran
	 */
	public static interface Executable
	{
		/**
		 * Execute method that can throw exception.
		 */
		public void execute() throws Exception;
	}
	
	/**
	 * Interface to represent tasks that can be submitted.
	 * @author akiran
	 */
	public static interface ExecutableWithReturn<T>
	{
		/**
		 * Execute method that can throw exception.
		 * @return any value as per need
		 */
		public T execute() throws Exception;
	}

	/**
	 * Executes the specified executable. In case of errors logs the error and ignores the error.
	 * @param executable executable to execute
	 * @param message message to be used in case of error for logging
	 * @param params params for message
	 */
	public static void execute(Executable executable, String message, Object... params)
	{
		try
		{
			executable.execute();
		} catch(Exception ex)
		{
			logger.warn(String.format(message, params), ex);
		}
	}
	
	/**
	 * Executes the specified executable. In case of errors logs the error and ignores the error.
	 * @param executable executable to execute
	 * @param message message to be used in case of error for logging
	 * @param params params for message
	 * @param <T> type of executable
	 * @return value returned by executor
	 */
	public static <T> T executeWithReturn(ExecutableWithReturn<T> executable, String message, Object... params)
	{
		try
		{
			return executable.execute();
		} catch(Exception ex)
		{
			logger.warn(String.format(message, params), ex);
			return null;
		}
	}

	/**
	 * Executes the specified executable. In case of errors logs the error and ignores the error.
	 * @param executable executable to execute
	 * @param defaultValue default value to be returned in case of error
	 * @param message message to be used in case of error for logging
	 * @param params params for message
	 * @param <T> type of executable
	 * @return value returned by executor
	 */
	public static <T> T executeWithReturn(ExecutableWithReturn<T> executable, T defaultValue, String message, Object... params)
	{
		try
		{
			return executable.execute();
		} catch(Exception ex)
		{
			logger.warn(String.format(message, params), ex);
			return defaultValue;
		}
	}
	
	/**
	 * Executes the specified executable just like {@link #execute(Executable)}. But this will throw {@link WeaverRuntimeException} when 
	 * an error occurs (by wrapping the actual error).
	 * @param executable executable to execute
	 * @param message message to be used in case of error for throwing runtime exception
	 * @param params params for message
	 */
	public static void executeWithError(Executable executable, String message, Object... params)
	{
		executeWithError(executable, false, message, params);
	}

	/**
	 * Executes the specified executable just like {@link #execute(Executable)}. But this will throw {@link WeaverRuntimeException} when 
	 * an error occurs (by wrapping the actual error).
	 * @param executable executable to execute
	 * @param skipRuntime if true, runtime exception also like checked exception will be wrapped with {@link WeaverRuntimeException}
	 * @param message message to be used in case of error for throwing runtime exception
	 * @param params params for message
	 */
	public static void executeWithError(Executable executable, boolean skipRuntime, String message, Object... params)
	{
		try
		{
			executable.execute();
		} catch(Exception ex)
		{
			if(!skipRuntime)
			{
				if(ex instanceof RuntimeException)
				{
					throw (RuntimeException) ex;
				}
			}
			
			throw new InvalidStateException(String.format(message, params), ex);
		}
	}

	/**
	 * Executes the specified executable just like {@link #execute(Executable)}. But this will throw {@link WeaverRuntimeException} when 
	 * an error occurs (by wrapping the actual error).
	 * @param executable executable to execute
	 * @param message message to be used in case of error for throwing runtime exception
	 * @param params params for message
	 * @param <T> type of executable
	 * @return value returned by executor
	 */
	public static <T> T executeWithErrorAndReturn(ExecutableWithReturn<T> executable, String message, Object... params)
	{
		try
		{
			return executable.execute();
		} catch(Exception ex)
		{
			if(ex instanceof RuntimeException)
			{
				throw (RuntimeException) ex;
			}
			
			throw new InvalidStateException(String.format(message, params), ex);
		}
	}
	
	/**
	 * Execute the specified executable with specified number of retries in case of error. If no error occurs this method
	 * return immediately. Before retry, the thread will be made to wait for specified delay.
	 *
	 * @param opMssg Message to be used for logging in case of errors
	 * @param maxRetries the max retries to be done in case of errors
	 * @param delayInMillis the delay in millis b/w retries
	 * @param executable the executable to be executed
	 */
	public static void executeWithRetry(String opMssg, int maxRetries, long delayInMillis, Executable executable) throws Exception 
	{
		Exception error = null;
		int delayIdx = maxRetries - 1;
		
		for(int i = 0; i < maxRetries; i++)
		{
			try
			{
				//execute the actual job
				executable.execute();
				
				//in case of success return immediately
				return;
			} catch(Exception ex)
			{
				error = ex;
				logger.warn("Failed to execute operation '{}'. Will retry after {} millis. [Retry {} of {}]", opMssg, delayInMillis, (i + 1), maxRetries, ex);
			}

			try
			{
				//for last index skip waiting
				if(i < delayIdx)
				{
					Thread.sleep(delayInMillis);
				}
			} catch(InterruptedException ex)
			{
				logger.warn("Ignoring interrupted exception and continuing with next retry");
			}
		}
		
		throw error;
	}
}
