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

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

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
	protected HttpUriRequestBase newRequest(URI resolvedUri) 
	{
		return new HttpPost(resolvedUri);
	}
}
