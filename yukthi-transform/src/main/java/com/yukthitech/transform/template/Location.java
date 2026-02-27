package com.yukthitech.transform.template;

import java.io.Serializable;

/**
 * Represents a location in a JSON document.
 */
public class Location implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The line number of the location.
	 */
	private int line;

	/**
	 * The column number of the location.
	 */
	private int column;

	/**
	 * The path to the location.
	 */
	private String path;

	public Location(int line, int column, String path)
	{
		this.line = line;
		this.column = column;
		this.path = path;
	}

	public int getLine()
	{
		return line;
	}

	public int getColumn()
	{
		return column;
	}

	public String getPath()
	{
		return path;
	}

	@Override
	public String toString()
	{
		return String.format("[Line: %d, Column: %d, Path: %s]", line, column, path);
	}
}
