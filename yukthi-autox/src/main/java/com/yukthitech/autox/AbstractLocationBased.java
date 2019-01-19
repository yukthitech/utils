package com.yukthitech.autox;

/**
 * Base class for location based classes.
 * @author akiran
 */
public abstract class AbstractLocationBased implements ILocationBased
{
	/**
	 * Location set by framework.
	 */
	private String location;
	
	@Override
	public void setLocation(String location)
	{
		this.location = location;
	}

	@Override
	public String getLocation()
	{
		return location;
	}
}
