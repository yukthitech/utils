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
	 * Fetches string from underlying stream and returns the same. This will close the underlying stream.
	 * @return content represented by this stream.
	 */
	public String toText();
	
	/**
	 * Closes the stream.
	 */
	public void close();
}
