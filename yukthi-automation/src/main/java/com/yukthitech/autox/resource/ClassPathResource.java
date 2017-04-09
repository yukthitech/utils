package com.yukthitech.autox.resource;

import java.io.InputStream;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * represents class path resource.
 * @author akiran
 */
public class ClassPathResource implements IResource
{
	private InputStream is;
	
	public ClassPathResource(String resource)
	{
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
