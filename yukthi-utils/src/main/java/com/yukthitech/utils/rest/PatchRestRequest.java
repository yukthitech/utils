/**
 * 
 */
package com.yukthitech.utils.rest;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPatch;

/**
 * Represents PUT request method
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
	
	/* (non-Javadoc)
	 * @see com.fw.utils.rest.RestRequestWithBody#newRequest(java.lang.String)
	 */
	@Override
	protected HttpEntityEnclosingRequestBase newRequest(String baseUrl) 
	{
		return new HttpPatch(baseUrl + getResolvedUri());
	}
}
