package com.yukthitech.autox.ide;

import java.io.File;

import com.yukthitech.autox.ide.model.Project;

/**
 * File details of the ide.
 * @author akiran
 */
public class FileDetails implements Comparable<FileDetails>
{
	private File file;
	
	private Project project;
	
	private String path;
	
	public FileDetails(File file, Project project)
	{
		this.file = file;
		this.project = project;
		
		this.path = IdeFileUtils.getRelativePath(project.getBaseFolder(), file);
	}

	public File getFile()
	{
		return file;
	}
	
	public Project getProject()
	{
		return project;
	}
	
	public String getPath()
	{
		return path;
	}

	@Override
	public int compareTo(FileDetails o)
	{
		int diff = file.getName().compareTo(o.file.getName());
		
		if(diff == 0)
		{
			return file.compareTo(o.file);
		}
		
		return diff;
	}

	@Override
	public String toString()
	{
		return file.getName();
	}
}
