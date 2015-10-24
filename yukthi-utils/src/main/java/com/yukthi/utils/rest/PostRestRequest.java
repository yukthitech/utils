/**
 * 
 */
package com.yukthi.utils.rest;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;

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
	protected HttpEntityEnclosingRequestBase newRequest(String baseUrl) 
	{
		return new HttpPost(baseUrl + getResolvedUri());
	}
}
