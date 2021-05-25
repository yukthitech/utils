/**
 * 
 */
package com.yukthitech.utils.rest;

import java.net.URI;

import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

/**
 * Represents PATCH request method
 * @author akiran
 */
public class PatchRestRequest extends RestRequestWithBody<PatchRestRequest>
{
	/**
	 * @param uri
	 */
	public PatchRestRequest(String uri)
	{
		super(uri, "PATCH");
	}

	@Override
	protected HttpUriRequestBase newRequest(URI resolvedUri) 
	{
		return new HttpPatch(resolvedUri);
	}
}
