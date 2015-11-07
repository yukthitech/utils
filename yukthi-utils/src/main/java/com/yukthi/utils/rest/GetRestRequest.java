/**
 * 
 */
package com.yukthi.utils.rest;

import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;

/**
 * Represent Rest GET request
 * @author akiran
 */
public class GetRestRequest extends RestRequest<GetRestRequest>
{
	/**
	 * @param uri
	 */
	public GetRestRequest(String uri)
	{
		super(uri);
	}
	
	/* (non-Javadoc)
	 * @see com.yodlee.common.rest.RestRequest#toHttpRequestBase(java.lang.String)
	 */
	@Override
	public HttpRequestBase toHttpRequestBase(String baseUrl) throws URISyntaxException
	{
		URIBuilder uriBuilder = new URIBuilder(baseUrl + getResolvedUri());
		
		for(String paramName : super.params.keySet())
		{
			uriBuilder.addParameter(paramName, super.params.get(paramName));
		}
		
		HttpGet httpget = new HttpGet(uriBuilder.build());
		super.populateHeaders(httpget);
		
		return httpget;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("\n\tRequest Type: GET");
		builder.append("\n\t").append("Resolved URI: ").append(super.getResolvedUri());
		builder.append("\n\t").append("Params: ").append(super.params);
		
		if(!headers.isEmpty())
		{
			builder.append("\n\t").append("Headers: ").append(super.headers);
		}
		

		builder.append("\n]");
		return builder.toString();
	}
}
