package com.yukthitech.autox.ide.model;

import java.io.Serializable;

/**
 * State of open project file.
 * @author akiran
 */
public class FileState implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Path of the open file.
	 */
	private String path;
	
	/**
	 * Position of the cursor in the file.
	 */
	private int cursorPositon;
	
	/**
	 * Instantiates a new file state.
	 */
	public FileState()
	{}

	/**
	 * Instantiates a new file state.
	 *
	 * @param path the path
	 * @param cursorPositon the cursor positon
	 */
	public FileState(String path, int cursorPositon)
	{
		this.path = path;
		this.cursorPositon = cursorPositon;
	}

	/**
	 * Gets the path of the open file.
	 *
	 * @return the path of the open file
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Sets the path of the open file.
	 *
	 * @param path the new path of the open file
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * Gets the position of the cursor in the file.
	 *
	 * @return the position of the cursor in the file
	 */
	public int getCursorPositon()
	{
		return cursorPositon;
	}

	/**
	 * Sets the position of the cursor in the file.
	 *
	 * @param cursorPositon the new position of the cursor in the file
	 */
	public void setCursorPositon(int cursorPositon)
	{
		this.cursorPositon = cursorPositon;
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

		if(!(obj instanceof FileState))
		{
			return false;
		}

		FileState other = (FileState) obj;
		return path.equals(other.path);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return path.hashCode();
	}
}
