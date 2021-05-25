/**
 * 
 */
package com.yukthitech.utils.rest;

import java.net.URI;

import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

/**
 * Represents PUT request method
 * @author akiran
 */
public class PutRestRequest extends RestRequestWithBody<PutRestRequest>
{
	/**
	 * @param uri
	 */
	public PutRestRequest(String uri)
	{
		super(uri, "PUT");
	}
	
	@Override
	protected HttpUriRequestBase newRequest(URI resolvedUri) 
	{
		return new HttpPut(resolvedUri);
	}
}
