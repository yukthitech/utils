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

/**
 * Listener for rest client to support call backs
 * @author akiran
 */
public interface IRestClientListener
{
	/**
	 * Called before making the request to the server (and before building actual http
	 * request object)
	 * @param request Request that is going to be sent to server
	 */
	public void prerequest(RestRequest<?> request);
	
	/**
	 * Invoked once response is received and converted to expected data type (wrapped by RestResult)
	 * @param request Request used
	 * @param result Result with response received.
	 */
	public void postrequest(RestRequest<?> request, RestResult<?> result);
}
