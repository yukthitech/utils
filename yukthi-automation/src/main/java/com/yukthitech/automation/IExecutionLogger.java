package com.yukthitech.automation;

/**
 * Logger that will be passed to validation steps that can be used for logging.
 * @author akiran
 */
public interface IExecutionLogger
{
	/**
	 * Fetches a sub logger of current logger. So that log messages on this sub-logger will
	 * get sub indented compared to parent logger. 
	 * @return Sub logger
	 */
	public IExecutionLogger getSubLogger();
	
	/**
	 * Used to log error messages as part of current execution.
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void error(String mssgTemplate, Object... args);
	
	/**
	 * Used to log error messages as part of current execution.
	 * @param th Throwable stack trace.
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void error(Throwable th, String mssgTemplate, Object... args);
	
	/**
	 * Used to log debug messages as part of current execution.
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void debug(String mssgTemplate, Object... args);
}
