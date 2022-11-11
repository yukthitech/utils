package com.yukthitech.autox.debug.common;

import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * Represents debug point.
 * @author akranthikiran
 */
public class DebugPoint implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Path of file in which debug point is added.
	 */
	private String filePath;
	
	/**
	 * Line number of this debug point.
	 */
	private int lineNumber;
	
	/**
	 * Condition of debug point.
	 */
	private String condition;
	
	public DebugPoint()
	{}
	
	public DebugPoint(String filePath, int lineNumber, String condition)
	{
		this.filePath = filePath;
		this.lineNumber = lineNumber;
		this.condition = condition;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	public String getCondition()
	{
		return condition;
	}

	public void setCondition(String condition)
	{
		this.condition = condition;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof DebugPoint))
		{
			return false;
		}

		DebugPoint other = (DebugPoint) obj;
		return filePath.equals(other.filePath) && (lineNumber == other.lineNumber);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return Objects.hashCode(filePath, lineNumber);
	}
}
