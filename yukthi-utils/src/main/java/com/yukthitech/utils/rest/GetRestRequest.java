/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.utils.rest;

import java.net.URI;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

import com.yukthitech.utils.PropertyAccessor;
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
			properties = PropertyAccessor.describe(bean);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while extracting bean properties - {}", bean, ex);
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
