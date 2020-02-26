package com.yukthitech.autox.ide;

import java.io.File;

import com.yukthitech.autox.ide.model.Project;

public class LinkWithLocation
{
	/**
	 * Project in which file is present.
	 */
	private Project project;
	
	/**
	 * File to which linking should be done.
	 */
	private File file;
	
	/**
	 * Position within the file.
	 */
	private int position;
	
	/**
	 * Link start position.
	 */
	private int linkStart;
	
	/**
	 * Link end position.
	 */
	private int linkEnd;

	/**
	 * Instantiates a new file location.
	 *
	 * @param project the project
	 * @param file the file
	 * @param position the position
	 * @param linkStart the link start
	 * @param linkEnd the link end
	 */
	public LinkWithLocation(Project project, File file, int position, int linkStart, int linkEnd)
	{
		this.project = project;
		this.file = file;
		this.position = position;
		this.linkStart = linkStart;
		this.linkEnd = linkEnd;
	}

	/**
	 * Gets the project in which file is present.
	 *
	 * @return the project in which file is present
	 */
	public Project getProject()
	{
		return project;
	}

	/**
	 * Gets the file to which linking should be done.
	 *
	 * @return the file to which linking should be done
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Gets the position within the file.
	 *
	 * @return the position within the file
	 */
	public int getPosition()
	{
		return position;
	}

	/**
	 * Gets the link start position.
	 *
	 * @return the link start position
	 */
	public int getLinkStart()
	{
		return linkStart;
	}

	/**
	 * Gets the link end position.
	 *
	 * @return the link end position
	 */
	public int getLinkEnd()
	{
		return linkEnd;
	}
}
