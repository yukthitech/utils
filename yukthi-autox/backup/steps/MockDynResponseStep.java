package com.yukthitech.autox.test.proxy.steps;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.test.Function;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Mocks the specified request (url + method) with dynamic response returned by specified function.
 */
@Executable(name = "mockDynResponse", group = Group.Mock, message = "Mocks the specified request (url + method) with dynamic response returned by specified function.")
public class MockDynResponseStep extends AbstractStep
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the server where mocking should be done.
	 */
	@Param(description = "Name of the server where mocking should be done.", required = true, sourceType = SourceType.EXPRESSION)
	private String name;

	/**
	 * Request uri to be mocked.
	 */
	@Param(description = "Request uri to be mocked", required = true, sourceType = SourceType.EXPRESSION)
	private String uri;

	/**
	 * Http method of the request to be mocked.
	 */
	@Param(description = "Http method of the request to be mocked", required = true, sourceType = SourceType.EXPRESSION)
	private String method;
	
	/**
	 * Steps to be executed on request. 
	 */
	@SkipParsing
	@Param(description = "Steps to be executed on request.")
	private Function onRequest;

	/**
	 * Sets the name of the server where mocking should be done.
	 *
	 * @param name the new name of the server where mocking should be done
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the request uri to be mocked.
	 *
	 * @param uri the new request uri to be mocked
	 */
	public void setUri(String uri)
	{
		this.uri = uri;
	}

	/**
	 * Sets Http method of the request to be mocked.
	 *
	 * @param method Http method of the request to be mocked
	 */
	public void setMethod(String method)
	{
		this.method = method;
	}
	
	/**
	 * Sets the steps to be executed on request.
	 *
	 * @param onRequest the new steps to be executed on request
	 */
	public void setOnRequest(Function onRequest)
	{
		this.onRequest = onRequest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.
	 * AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		logger.debug(true, "Adding mock response for [Method: {}, Uri: {}] as [Status code: {}, <br>Headers: {}, <br>Body: {}, <br>Wait Config: {}]", 
				method, uri, responseStatusCode, responseHeaders, responseBody, waitConfig);
		
		function
		
		MockResponse response = new MockResponse(uri, method, responseHeaders, Integer.parseInt(responseStatusCode), responseBody, waitConfig);
		response.setCountLeft(times);

		logger.debug("On server '{}' mocking response {}", name, response);
		
		MockServer server = MockServerFactory.getMockServer(name);
		server.addMockResponse(response);

		return true;
	}
}
