package com.yukthitech.autox.resource;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * represents class path resource.
 * @author akiran
 */
public class ClassPathResource implements IResource
{
	/**
	 * Resource path.
	 */
	private String resource;
	
	/**
	 * Input stream of resource.
	 */
	private InputStream is;
	
	public ClassPathResource(String resource)
	{
		this.resource = resource;
		is = ClassPathResource.class.getResourceAsStream(resource);
		
		if(is == null)
		{
			throw new InvalidArgumentException("Invalid classpath resource specified: {}", resource);
		}
	}

	@Override
	public InputStream getInputStream()
	{
		return is;
	}
	
	@Override
	public String toText()
	{
		try
		{
			String content = IOUtils.toString(is, (String) null);
			is.close();
			
			return content;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading string content from undelying resource - {}", resource, ex);
		}
	}

	@Override
	public void close()
	{
		try
		{
			is.close();
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("An error occurred while closing classpath resource", ex);
		}
	}
}
