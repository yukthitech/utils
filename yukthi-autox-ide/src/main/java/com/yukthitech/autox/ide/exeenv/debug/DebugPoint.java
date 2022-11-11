package com.yukthitech.autox.ide.exeenv.debug;

import java.io.File;
import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * Represents a debug point.
 * @author akranthikiran
 */
public class DebugPoint implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Project of source file in which debug point is created.
	 */
	private String project;
	
	/**
	 * File in which debug point is created.
	 */
	private File file;
	
	/**
	 * Line number at which debug point is created.
	 */
	private int lineNo;
	
	public DebugPoint(String project, File file, int lineNo)
	{
		this.project = project;
		this.file = file;
		this.lineNo = lineNo;
	}

	public String getProject()
	{
		return project;
	}

	public File getFile()
	{
		return file;
	}
	
	public void setLineNo(int lineNo)
	{
		this.lineNo = lineNo;
	}

	public int getLineNo()
	{
		return lineNo;
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
		return file.equals(other.file) && lineNo == other.lineNo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return Objects.hashCode(file, lineNo);
	}
}
