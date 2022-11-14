package com.yukthitech.autox.test.proxy.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Resets specified mock server.
 * @author akiran
 */
@Executable(name = "mockServerReset", group = Group.Mock, message = "Resets specified mock server, that is cleaning up all mocked responses and requests..")
public class ResetMockServerStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the server.
	 */
	@Param(description = "Name of the server.", required = true, sourceType = SourceType.EXPRESSION)
	private String name;

	/**
	 * Sets the name of the server.
	 *
	 * @param name
	 *            the new name of the server
	 */
	public void setName(String name)
	{
		this.name = name;
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
		logger.debug("Reseting mock server: {}", name);
		
		MockServer server = MockServerFactory.getMockServer(name);
		server.reset();
	}
}
