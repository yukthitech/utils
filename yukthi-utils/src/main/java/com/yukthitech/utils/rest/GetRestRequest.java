/**
 * 
 */
package com.yukthitech.utils.rest;

import java.net.URI;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represent Rest GET request
 * @author akiran
 */
public class GetRestRequest extends RestRequestWithBody<GetRestRequest>
{
	/**
	 * @param uri
	 */
	public GetRestRequest(String uri)
	{
		super(uri, "GET");
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
	
	@Override
	protected HttpUriRequestBase newRequest(URI resolvedUri) 
	{
		return new HttpGet(resolvedUri);
	}
}
