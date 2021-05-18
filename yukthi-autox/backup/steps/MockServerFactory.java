package com.yukthitech.autox.test.proxy.steps;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Factor of mock servers.
 * @author akiran
 */
public class MockServerFactory
{
	/**
	 * Name to server.
	 */
	private static Map<String, MockServer> nameToServer = new HashMap<>();
	
	/**
	 * Starts a server with specified name at specified port.
	 * @param context context to be used
	 * @param name name of server
	 * @param port port at which server needs to be started.
	 */
	public static synchronized void startMockServer(AutomationContext context, String name, int port)
	{
		MockServer server = nameToServer.get(name);
		ExecutionLogger logger = context.getExecutionLogger();
		
		if(server != null)
		{
			if(server.getPort() != port)
			{
				logger.error("Server with name '{}' is already running at port {}. So it can not be started at: {}", name, server.getPort(), port);
				throw new InvalidStateException("Server with name '{}' is already started at port: {}", name, server.getPort());
			}
			
			logger.debug("Server with name '{}' is already running at port: {}. So ignoring to start same server on same port", name, port);
			return;
		}
		
		server = new MockServer(port);
		nameToServer.put(name, server);
		
		logger.debug("Started server with name '{}' at port: {}", name, port);
	}
	
	/**
	 * Stops specified mock server.
	 * @param context
	 * @param name
	 */
	public static synchronized void stopMockServer(AutomationContext context, String name)
	{
		MockServer server = nameToServer.get(name);
		ExecutionLogger logger = context.getExecutionLogger();
		
		if(server == null)
		{
			logger.debug("No server found with name: {}. Ignoring stop request.", name);
			return;
		}
		
		server.stop();
		nameToServer.remove(name);
		logger.debug("Server '{}' is stopped successfully", name);
	}
	
	/**
	 * Fetches the mock server with specified name.
	 * @param name
	 * @return
	 */
	public static synchronized MockServer getMockServer(String name)
	{
		MockServer server = nameToServer.get(name);
		
		if(server == null)
		{
			throw new InvalidArgumentException("No server found with name: {}", name);
		}
		
		return server;
	}
}
