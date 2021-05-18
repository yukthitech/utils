package com.yukthitech.autox.test.proxy.steps;

/**
 * Handler that wraps a single response and returns the same every time.
 * @author akiran
 */
public class DummyWrapperHandler implements IMockRequestHandler
{
	/**
	 * Response being wrapped.
	 */
	private MockResponse response;

	public DummyWrapperHandler(MockResponse response)
	{
		this.response = response;
	}
	
	@Override
	public boolean isMatchingRequest(MockRequest request)
	{
		return response.isMatchingRequest(request);
	}
	
	@Override
	public MockResponse handle(MockRequest request)
	{
		return response;
	}
	
	@Override
	public void stop()
	{
		response.stop();
	}
}
