package com.yukthitech.autox.resource;

import java.io.InputStream;

/**
 * Represents resource that can be load.
 * @author akiran
 */
public interface IResource
{
	/**
	 * opens the resource.
	 * @return Input stream to read resource.
	 */
	public InputStream getInputStream();
	
	/**
	 * Closes the stream.
	 */
	public void close();
}
