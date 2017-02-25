/**
 * 
 */
package com.yukthitech.utils.rest;

import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;

import com.yukthitech.utils.exceptions.InvalidStateException;

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
	
	/**
	 * Adds properties of specified bean as request parameters
	 * @param bean Bean from which properties needs to be extracted
	 */
	public void addBeanParameters(Object bean)
	{
		//if bean is null, return
		if(bean == null)
		{
			return;
		}
		
		//extract bean properties as map
		Map<String, Object> properties = null;
		
		try
		{
			properties = PropertyUtils.describe(bean);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while extracting bean properties - {}", bean);
		}
		
		//add extracted properties as request params
		for(String name : properties.keySet())
		{
			super.addParam(name, "" + properties.get(name));
		}
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
