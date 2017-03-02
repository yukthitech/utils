package com.yukthitech.automation;

import java.io.File;

/**
 * Logger that will be passed to validation steps that can be used for logging.
 * @author akiran
 */
public interface IExecutionLogger
{
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
	
	/**
	 * Adds the specified image file to the debug log.
	 * @param name Name of the image file for easy identification
	 * @param message Message to be logged along with image
	 * @param imageFile Image to be logged
	 */
	public void logImage(String name, String message, File imageFile);
}
