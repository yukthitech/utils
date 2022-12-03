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
package com.yukthitech.autox.test.proxy.steps;

/**
 * Handler to handle mock request and return response.
 * @author akiran
 */
public interface IMockRequestHandler
{
	/**
	 * Checks if specified request can be handler by this handler or not.
	 * @param request request to be checked
	 * @return true if request can be handled.
	 */
	public boolean isMatchingRequest(MockRequest request);
	
	/**
	 * Handles the specified mock request and returns the result.
	 * @param request request to handle/process
	 * @return result mock response
	 */
	public MockResponse handle(MockRequest request);
	
	/**
	 * Stops the underlying threads/process.
	 */
	public void stop();
}
