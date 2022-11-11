package com.yukthitech.autox.debug.server;

import java.io.File;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Throw when debug point is reached.
 * 
 * @author akranthikiran
 */
public class DebugPointReachedException extends UtilsException
{
	private static final long serialVersionUID = 1L;
	
	private File location;
	
	private int lineNumber;

	public DebugPointReachedException(File location, int lineNo)
	{
		super("Debug point reached");
		
		this.location = location;
		this.lineNumber = lineNo;
	}

	public File getLocation()
	{
		return location;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}
}
