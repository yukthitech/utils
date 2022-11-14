package com.yukthitech.autox.test.proxy.steps;

import java.util.List;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Fetches mock request details with specified filter details.
 * 
 * @author akiran
 */
@Executable(name = "mockFetchRequest", group = Group.Mock, message = "Fetches mock request details with specified filter details")
public class MockFetchRequestStep extends AbstractStep
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the server.
	 */
	@Param(description = "Name of the server.", required = true)
	private String name;

	/**
	 * Url filter to be used to fetch mock request.
	 */
	@Param(description = "Uri filter to be used to fetch mock request.", required = false, sourceType = SourceType.EXPRESSION)
	private String uriFilter;

	/**
	 * Method filter to be used to fetch mock request.
	 */
	@Param(description = "Method filter to be used to fetch mock request.", required = false, sourceType = SourceType.EXPRESSION)
	private String methodFilter;

	/**
	 * Attribute name to be used to store filtered-request on context.
	 */
	@Param(description = "Attribute name to be used to store filtered-request on context.", required = true, attrName = true)
	private String attributeName;

	/**
	 * Sets the name of the server.
	 *
	 * @param name the new name of the server
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the url filter to be used to fetch mock request.
	 *
	 * @param urlFilter the new url filter to be used to fetch mock request
	 */
	public void setUriFilter(String uriFilter)
	{
		this.uriFilter = uriFilter;
	}

	/**
	 * Sets the method filter to be used to fetch mock request.
	 *
	 * @param methodFilter the new method filter to be used to fetch mock request
	 */
	public void setMethodFilter(String methodFilter)
	{
		this.methodFilter = methodFilter;
	}

	/**
	 * Sets the attribute name to be used to store filtered-request on context.
	 *
	 * @param attributeName the new attribute name to be used to store filtered-request on context
	 */
	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.
	 * AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger logger) throws Exception
	{
		MockRequestFilter filter = new MockRequestFilter(uriFilter, methodFilter);

		logger.debug("Fetching mock-requests from server '{}' with filter: {}", name, filter);

		MockServer mockServer = MockServerFactory.getMockServer(name);
		List<MockRequest> requests = mockServer.fetchRequests(filter);

		logger.debug("From server '{}' number of requests filtered are {}. Setting requests as attribute: {}", name, requests.size(), attributeName);
		context.setAttribute(attributeName, requests);
	}
}
