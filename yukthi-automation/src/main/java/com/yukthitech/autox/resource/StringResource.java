package com.yukthitech.autox.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Represents string resource.
 * @author akiran
 */
public class StringResource implements IResource
{
	/**
	 * String input representing the resource.
	 */
	private String content;
	
	public StringResource(String content)
	{
		this.content = content;
	}
	
	@Override
	public InputStream getInputStream()
	{
		return new ByteArrayInputStream(content.getBytes());
	}

	@Override
	public void close()
	{
	}
}
