package com.yukthitech.autox;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Immutable Wrapper over the file. 
 * @author akranthikiran
 */
public class ReportLogFile
{
	private File file;

	ReportLogFile(File file)
	{
		this.file = file;
	}
	
	public void copyContent(File source)
	{
		try
		{
			FileUtils.copyURLToFile(source.toURI().toURL(), file);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while copying content", ex);
		}
	}
	
	public File getFile()
	{
		return file;
	}
	
	@Override
	public String toString()
	{
		return file.toString();
	}
}
