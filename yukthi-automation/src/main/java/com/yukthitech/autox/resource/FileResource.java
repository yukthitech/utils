package com.yukthitech.autox.resource;

import java.io.FileInputStream;
import java.io.InputStream;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents file resource.
 * @author akiran
 */
public class FileResource implements IResource
{
	/**
	 * File input stream representing the resource.
	 */
	private FileInputStream fis;
	
	public FileResource(String resource)
	{
		try
		{
			this.fis = new FileInputStream(resource);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to load file resource - {}", resource);
		}
	}
	
	@Override
	public InputStream getInputStream()
	{
		return fis;
	}

	@Override
	public void close()
	{
		try
		{
			fis.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to close file resource", ex);
		}
	}
}
