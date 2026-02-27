package com.yukthitech.transform.template;

import java.io.Serializable;

/**
 * Represents a location in a JSON document.
 */
public class Location implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the template.
	 */
	private String templateName;
	
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

	public Location(String templateName, int line, int column, String path)
	{
		this.templateName = templateName;
		this.line = line;
		this.column = column;
		this.path = path;
	}
	
	public Location sublocation(String subpath)
	{
		return new Location(subpath, line, column, path + subpath);
	}

	public String getTemplateName()
	{
		return templateName;
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
		return String.format("[Template: %s, Line: %d, Column: %d, Path: %s]", templateName, line, column, path);
	}
}
