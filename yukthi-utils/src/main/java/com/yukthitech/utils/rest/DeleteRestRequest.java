/**
 * 
 */
package com.yukthitech.utils.rest;

import java.net.URI;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

/**
 * Represent Rest DELETE request
 * @author akiran
 */
public class DeleteRestRequest extends RestRequestWithBody<DeleteRestRequest>
{
	/**
	 * @param uri
	 */
	public DeleteRestRequest(String uri)
	{
		super(uri, "DELETE");
	}
	
	@Override
	protected HttpUriRequestBase newRequest(URI resolvedUri) 
	{
		return new HttpDelete(resolvedUri);
	}
}
