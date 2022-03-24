package com.yukthitech.autox.exec;

/**
 * Handler for exceptions that occurs during step execution.
 * @author akranthikiran
 */
public interface IExceptionHandler
{
	/**
	 * Called when a step under current execution results in failure or error.
	 * This method should return true, if the exception is expected and should not be considered as error/failure.
	 * By returning false, the regular exception handling occurs, which would mark current execution as error/failure approp. 
	 *  
	 * @param stackEntry current stack entry
	 * @param ex Exception that occurred during execution
	 * @return true if exception is handled and should not be considered as error/failure.
	 */
	public boolean handleError(IExecutionStackEntry stackEntry, Exception ex);
}
