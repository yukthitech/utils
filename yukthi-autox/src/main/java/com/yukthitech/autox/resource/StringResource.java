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
	 * Name of the source resource.
	 */
	private String name;

	/**
	 * String input representing the resource.
	 */
	private String content;
	
	/**
	 * Flag indicating if this is raw type resource.
	 */
	private boolean rawType;

	public StringResource(String name, String content, boolean rawType)
	{
		this.name = name;
		this.content = content;
		this.rawType = rawType;
	}

	public StringResource(String content, boolean rawType)
	{
		this.content = content;
		this.rawType = rawType;
	}
	
	@Override
	public InputStream getInputStream()
	{
		return new ByteArrayInputStream(content.getBytes());
	}

	@Override
	public String toText()
	{
		return content;
	}

	@Override
	public void close()
	{
	}

	@Override
	public boolean isRawType()
	{
		return rawType;
	}

	@Override
	public String getName()
	{
		return name;
	}
}
