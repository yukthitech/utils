package com.yukthitech.autox.logmon;

import java.io.File;

/**
 * Encapsulation of name and file.
 * @author akiran
 */
public class LogFile
{
	/**
	 * Name of the log file.
	 */
	private String name;
	
	/**
	 * File where the log data is dumped.
	 */
	private File file;
	
	/**
	 * Position of the file, used for internal purpose.
	 */
	private long position;
	
	public LogFile()
	{}
	
	public LogFile(String name, File file)
	{
		this.name = name;
		this.file = file;
	}

	/**
	 * Gets the name of the log file.
	 *
	 * @return the name of the log file
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the log file.
	 *
	 * @param name the new name of the log file
	 */
	void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the file where the log data is dumped.
	 *
	 * @return the file where the log data is dumped
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Sets the file where the log data is dumped.
	 *
	 * @param file the new file where the log data is dumped
	 */
	void setFile(File file)
	{
		this.file = file;
	}

	/**
	 * Gets the position of the attribute, used for internal purpose.
	 *
	 * @return the position of the attribute, used for internal purpose
	 */
	long getPosition()
	{
		return position;
	}

	/**
	 * Sets the position of the attribute, used for internal purpose.
	 *
	 * @param position the new position of the attribute, used for internal purpose
	 */
	void setPosition(long position)
	{
		this.position = position;
	}
}
