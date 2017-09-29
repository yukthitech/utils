package com.yukthitech.autox;

/**
 * Indicates the target item has location which can be used for logging.
 * @author akiran
 */
public interface ILocationBased
{
	/**
	 * Called by framework to set the location of step.
	 * @param location location of the step
	 */
	public void setLocation(String location);
	
	/**
	 * Used by framework to get the location of step for logging messages.
	 * @return location of step.
	 */
	public String getLocation();
}
