package com.yukthitech.autox;

import java.io.File;

/**
 * Indicates the target item has location which can be used for logging.
 * @author akiran
 */
public interface ILocationBased
{
	/**
	 * Called by framework to set the location of step.
	 * @param location location of the step
	 * @param lineNumber line in which this is defined.
	 */
	public void setLocation(File location, int lineNumber);
	
	/**
	 * Used by framework to get the location of step for logging messages.
	 * @return location of step.
	 */
	public File getLocation();
	
	/**
	 * Line number where this is defined.
	 * @return line number
	 */
	public int getLineNumber();
}
