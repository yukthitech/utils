package com.yukthitech.autox.test.proxy.steps;

import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Starts Mock Server with specified name and port.
 */
@Executable(name = "mockServerStart", group = Group.Mock, message = "Starts Mock Server with specified name and port")
public class StartMockServerStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the server.
	 */
	@Param(description = "Name of the server.", required = true, sourceType = SourceType.EXPRESSION)
	private String name;

	/**
	 * Port Number on which mock server has to start.
	 */
	@Param(description = "Port Number on which mock server has to start", required = true, sourceType = SourceType.EXPRESSION)
	private String port;

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
	 * Sets the port Number on which mock server has to start.
	 *
	 * @param port the new port Number on which mock server has to start
	 */
	public void setPort(String port)
	{
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger logger) throws Exception
	{
		logger.debug("Starting Mock Server '{}' on port: {}", name, port);

		int port = -1;
		
		try
		{
			port = Integer.parseInt(this.port);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid port value specified: " + port);
		}
		
		MockServerFactory.startMockServer(context, name, port);
	}
}
