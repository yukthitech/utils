package com.yukthitech.autox.ide.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * State of the project.
 * @author akiran
 */
public class ProjectState implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the open project.
	 */
	private String path;
	
	/**
	 * Open files state.
	 */
	private Set<FileState> openFiles = new LinkedHashSet<>();
	
	/**
	 * Instantiates a new project state.
	 */
	public ProjectState()
	{}
	
	/**
	 * Instantiates a new project state.
	 *
	 * @param path the name
	 */
	public ProjectState(String path)
	{
		this.path = path;
	}

	/**
	 * Gets the name of the open project.
	 *
	 * @return the name of the open project
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Sets the name of the open project.
	 *
	 * @param path the new name of the open project
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * Gets the open files state.
	 *
	 * @return the open files state
	 */
	public Set<FileState> getOpenFiles()
	{
		return openFiles;
	}

	/**
	 * Sets the open files state.
	 *
	 * @param openFiles the new open files state
	 */
	public void setOpenFiles(Set<FileState> openFiles)
	{
		if(openFiles != null)
		{
			this.openFiles.addAll(openFiles);
		}
	}
	
	/**
	 * Adds open file to project state.
	 * @param fileState
	 */
	public void addOpenFile(FileState fileState)
	{
		this.openFiles.add(fileState);
	}
	
	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
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

		if(!(obj instanceof ProjectState))
		{
			return false;
		}

		ProjectState other = (ProjectState) obj;
		return path.equals(other.path);
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return path.hashCode();
	}
}