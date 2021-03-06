package com.yukthitech.autox.resource;

import java.io.InputStream;

/**
 * Represents resource that can be load.
 * @author akiran
 */
public interface IResource
{
	/**
	 * Fetches the name of resource if any.
	 * @return name of the resource.
	 */
	public String getName();
	
	/**
	 * opens the resource.
	 * @return Input stream to read resource.
	 */
	public InputStream getInputStream();
	
	/**
	 * Fetches string from underlying stream and returns the same. This will close the underlying stream.
	 * @return content represented by this stream.
	 */
	public String toText();
	
	/**
	 * Fetches flag indicating if this resource is raw type.
	 * @return
	 */
	public boolean isRawType();
	
	/**
	 * Closes the stream.
	 */
	public void close();
}
