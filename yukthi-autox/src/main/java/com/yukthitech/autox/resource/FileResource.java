package com.yukthitech.autox.resource;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents file resource.
 * @author akiran
 */
public class FileResource implements IResource
{
	/**
	 * file path.
	 */
	private String resource;

	/**
	 * File input stream representing the resource.
	 */
	private FileInputStream fis;
	
	/**
	 * Flag indicating if this is raw type resource.
	 */
	private boolean rawType;
	
	public FileResource(String resource, boolean rawType)
	{
		this.resource = resource.trim();
		this.rawType = rawType;
		
		try
		{
			this.fis = new FileInputStream(this.resource);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to load file resource - {}", this.resource, ex);
		}
	}
	
	@Override
	public InputStream getInputStream()
	{
		return fis;
	}

	
	@Override
	public String toText()
	{
		try
		{
			String content = IOUtils.toString(fis, (String) null);
			fis.close();
			
			return content;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading string content from undelying file-resource - {}", resource, ex);
		}
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

	@Override
	public boolean isRawType()
	{
		return rawType;
	}

	@Override
	public String getName()
	{
		return resource;
	}
}
