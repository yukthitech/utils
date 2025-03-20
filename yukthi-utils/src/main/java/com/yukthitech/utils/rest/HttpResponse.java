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

import java.util.Iterator;
import java.util.Locale;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.ProtocolVersion;

/**
 * Wrapper over http response object. Used to isolate the users of this library from
 * http client classes.
 * @author akranthikiran
 */
public class HttpResponse
{
	private ClassicHttpResponse httpResponse;
	
	public HttpResponse(ClassicHttpResponse httResponse)
	{
		this.httpResponse = httResponse;
	}

	public HttpEntity getEntity()
	{
		return httpResponse.getEntity();
	}

	public boolean containsHeader(String name)
	{
		return httpResponse.containsHeader(name);
	}

	public int getCode()
	{
		return httpResponse.getCode();
	}
	
	public String getStatusMessage()
	{
		return httpResponse.getReasonPhrase();
	}

	public int countHeaders(String name)
	{
		return httpResponse.countHeaders(name);
	}

	public String getReasonPhrase()
	{
		return httpResponse.getReasonPhrase();
	}

	public ProtocolVersion getVersion()
	{
		return httpResponse.getVersion();
	}

	public Header getFirstHeader(String name)
	{
		return httpResponse.getFirstHeader(name);
	}

	public Locale getLocale()
	{
		return httpResponse.getLocale();
	}

	public Header getHeader(String name) throws ProtocolException
	{
		return httpResponse.getHeader(name);
	}

	public Header[] getHeaders()
	{
		return httpResponse.getHeaders();
	}

	public Header[] getHeaders(String name)
	{
		return httpResponse.getHeaders(name);
	}

	public Header getLastHeader(String name)
	{
		return httpResponse.getLastHeader(name);
	}

	public Iterator<Header> headerIterator()
	{
		return httpResponse.headerIterator();
	}

	public Iterator<Header> headerIterator(String name)
	{
		return httpResponse.headerIterator(name);
	}
}
