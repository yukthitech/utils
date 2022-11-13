package com.yukthitech.autox;

import java.io.File;

/**
 * Base class for location based classes.
 * @author akiran
 */
public abstract class AbstractLocationBased implements ILocationBased
{
	/**
	 * Location set by framework.
	 */
	private File location;
	
	/**
	 * Line number.
	 */
	private int lineNumber;
	
	@Override
	public void setLocation(File location, int lineNumber)
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
	public File getLocation()
	{
		return location;
	}
}
