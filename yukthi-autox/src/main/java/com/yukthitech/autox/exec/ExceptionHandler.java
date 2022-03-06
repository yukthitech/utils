package com.yukthitech.autox.exec;

/**
 * Handler for exceptions that occurs during step execution.
 * @author akranthikiran
 */
public interface ExceptionHandler
{
	public boolean handleError(Exception ex);
}
