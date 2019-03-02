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
	
	/**
	 * Line number.
	 */
	private int lineNumber;
	
	@Override
	public void setLocation(String location, int lineNumber)
	{
		this.location = location;
		this.lineNumber = lineNumber;
	}
	
	@Override
	public int getLineNumber()
	{
		return lineNumber;
	}

	@Override
	public String getLocation()
	{
		return location;
	}
}
