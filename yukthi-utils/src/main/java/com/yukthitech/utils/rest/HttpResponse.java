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
	private ClassicHttpResponse httResponse;
	
	public HttpResponse(ClassicHttpResponse httResponse)
	{
		this.httResponse = httResponse;
	}

	public HttpEntity getEntity()
	{
		return httResponse.getEntity();
	}

	public boolean containsHeader(String name)
	{
		return httResponse.containsHeader(name);
	}

	public int getCode()
	{
		return httResponse.getCode();
	}

	public int countHeaders(String name)
	{
		return httResponse.countHeaders(name);
	}

	public String getReasonPhrase()
	{
		return httResponse.getReasonPhrase();
	}

	public ProtocolVersion getVersion()
	{
		return httResponse.getVersion();
	}

	public Header getFirstHeader(String name)
	{
		return httResponse.getFirstHeader(name);
	}

	public Locale getLocale()
	{
		return httResponse.getLocale();
	}

	public Header getHeader(String name) throws ProtocolException
	{
		return httResponse.getHeader(name);
	}

	public Header[] getHeaders()
	{
		return httResponse.getHeaders();
	}

	public Header[] getHeaders(String name)
	{
		return httResponse.getHeaders(name);
	}

	public Header getLastHeader(String name)
	{
		return httResponse.getLastHeader(name);
	}

	public Iterator<Header> headerIterator()
	{
		return httResponse.headerIterator();
	}

	public Iterator<Header> headerIterator(String name)
	{
		return httResponse.headerIterator(name);
	}
}
