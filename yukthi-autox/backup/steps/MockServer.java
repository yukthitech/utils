package com.yukthitech.autox.test.proxy.steps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Mock server wrapping the jetty server. This maintains the responses being mocked
 * and requests received (till reset() is called).
 * @author akiran
 */
public class MockServer 
{
	/**
	 * Request handler for mock requests.
	 * @author akiran
	 */
	class MockRequestHandler extends AbstractHandler
	{
		@Override
		public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException 
		{
			handleRequest(arg0, arg1, request, response);
		}
	}

	/**
	 * Port at which server is running.
	 */
	private int port;
	
	/**
	 * Instance of jetty server.
	 */
	private Server server = null;
	
	/**
	 * Url to response mapping.
	 */
	private List<IMockRequestHandler> mockHandlers = new LinkedList<>();
	
	/**
	 * Mock requests received on this server.
	 */
	private List<MockRequest> mockRequests = new LinkedList<>();
	
	/**
	 * Instantiates a new mock server.
	 */
	MockServer(int port)
	{
		if(port < 0)
		{
			throw new IllegalStateException("Invalid port number specified: " + port);
		}
		
		this.port = port;

		server = new Server(port);
		server.getConnectors()[0].getConnectionFactory(HttpConnectionFactory.class);
		server.setHandler(new MockRequestHandler());
		
		try 
		{
			server.start();
		} catch (Exception ex) 
		{
			throw new InvalidStateException("An error occurred while starting mock-server at port: {}", port, ex);
		}
	}
	
	/**
	 * Gets the port at which server is running.
	 *
	 * @return the port at which server is running
	 */
	public int getPort()
	{
		return port;
	}
	
	/**
	 * Stop the jetty server.
	 */
	void stop()
	{
		try 
		{
			server.stop();
		} catch (Exception e) 
		{
			throw new InvalidStateException("An error occurred while stopping the server", e);
		}
	}
	
	/**
	 * Adds the specified mock response to the start of the queue.
	 * @param response
	 */
	public synchronized void addMockResponse(MockResponse response)
	{
		mockHandlers.add(0, response);
	}
	
	/**
	 * Fetches requests which are filtered by specified filter.
	 * @param filter
	 * @return
	 */
	public synchronized List<MockRequest> fetchRequests(MockRequestFilter filter)
	{
		List<MockRequest> filteredRequests = new ArrayList<>();
		
		for(MockRequest request : this.mockRequests)
		{
			if(filter.isMatching(request))
			{
				filteredRequests.add(request);
			}
		}
		
		return filteredRequests;
	}
	
	public synchronized void reset()
	{
		mockHandlers.forEach(resp -> resp.stop());
		
		mockRequests.clear();
		mockHandlers.clear();
	}
	
	private synchronized void handleRequest(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException 
	{
		MockRequest mockRequest = new MockRequest(request);
		Iterator<IMockRequestHandler> responseIt = mockHandlers.iterator();
		
		while(responseIt.hasNext())
		{
			IMockRequestHandler mockHandler = responseIt.next();
			
			if(!mockHandler.isMatchingRequest(mockRequest))
			{
				continue;
			}
			
			MockResponse mockResponse = mockHandler.handle(mockRequest);
			
			//if response is not served successfully
			if(!mockResponse.writeTo(response))
			{
				return;
			}
			
			if(!mockResponse.canServeMore())
			{
				responseIt.remove();
			}
			
			mockRequest.setMockResponse(mockResponse);
			mockRequests.add(mockRequest);
			
			return;
		}
		
		response.sendError(HttpServletResponse.SC_NOT_FOUND, "No mock response found for specified request. Time Stamp: " + System.currentTimeMillis());
	}
}
