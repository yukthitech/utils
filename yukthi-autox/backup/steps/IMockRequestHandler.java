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
