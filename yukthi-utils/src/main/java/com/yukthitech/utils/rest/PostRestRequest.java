/**
 * 
 */
package com.yukthitech.utils.rest;

import java.net.URI;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

/**
 * Represents post request method
 * @author akiran
 */
public class PostRestRequest extends RestRequestWithBody<PostRestRequest>
{
	/**
	 * @param uri
	 */
	public PostRestRequest(String uri)
	{
		super(uri, "POST");
	}
	
	/* (non-Javadoc)
	 * @see com.fw.utils.rest.RestRequestWithBody#newRequest(java.lang.String)
	 */
	@Override
	protected HttpUriRequestBase newRequest(URI resolvedUri) 
	{
		return new HttpPost(resolvedUri);
	}
}
