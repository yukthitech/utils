package com.yukthitech.utils.rest;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A simple mock server for testing REST client.
 * @author akranthikiran
 */
public class MockServer
{
	private Server server;
	private int port;
	
	private Map<String, Map<String, Object>> data = new ConcurrentHashMap<>();
	private Map<String, AtomicInteger> idCounters = new ConcurrentHashMap<>();
	private ObjectMapper objectMapper = new ObjectMapper();

	public MockServer(int port)
	{
		this.port = port;
	}

	public void start() throws Exception
	{
		server = new Server(port);
		server.setHandler(new RequestHandler());
		server.start();
	}

	public void stop() throws Exception
	{
		if(server != null)
		{
			server.stop();
		}
	}

	private class RequestHandler extends AbstractHandler
	{
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
		{
			String method = request.getMethod();
			String[] pathParts = target.split("/");

			if (pathParts.length < 3 || !pathParts[1].equals("entity"))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				baseRequest.setHandled(true);
				return;
			}

			String type = pathParts[2];
			String id = (pathParts.length > 3) ? pathParts[3] : null;

			try
			{
				switch (method)
				{
					case "POST":
						handlePost(type, request, response);
						break;
					case "GET":
						handleGet(type, id, response);
						break;
					case "PUT":
						handlePut(type, id, request, response);
						break;
					case "DELETE":
						handleDelete(type, id, response);
						break;
					default:
						response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				}
			}
			catch (Exception ex)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().write(ex.getMessage());
			}
			finally
			{
				baseRequest.setHandled(true);
			}
		}
		
		@SuppressWarnings("unchecked")
		private void handlePost(String type, HttpServletRequest request, HttpServletResponse response) throws IOException
		{
			Map<String, Object> typeData = data.computeIfAbsent(type, k -> new ConcurrentHashMap<>());
			AtomicInteger idCounter = idCounters.computeIfAbsent(type, k -> new AtomicInteger(1));
			
			Map<String, Object> newObject = objectMapper.readValue(request.getInputStream(), Map.class);
			String newId = Integer.toString(idCounter.getAndIncrement());
			newObject.put("id", newId);
			
			typeData.put(newId, newObject);
			
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setContentType("application/json");
			objectMapper.writeValue(response.getWriter(), newObject);
		}

		private void handleGet(String type, String id, HttpServletResponse response) throws IOException
		{
			Map<String, Object> typeData = data.get(type);
			
			if(typeData == null)
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			if(id == null)
			{
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				objectMapper.writeValue(response.getWriter(), typeData.values());
				return;
			}
			
			Object object = typeData.get(id);
			
			if(object == null)
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			objectMapper.writeValue(response.getWriter(), object);
		}

		@SuppressWarnings("unchecked")
		private void handlePut(String type, String id, HttpServletRequest request, HttpServletResponse response) throws IOException
		{
			Map<String, Object> typeData = data.get(type);
			
			if(typeData == null || !typeData.containsKey(id))
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			Map<String, Object> updatedData = objectMapper.readValue(request.getInputStream(), Map.class);
			updatedData.put("id", id);
			typeData.put(id, updatedData);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			objectMapper.writeValue(response.getWriter(), updatedData);
		}

		private void handleDelete(String type, String id, HttpServletResponse response)
		{
			Map<String, Object> typeData = data.get(type);
			
			if(typeData == null || !typeData.containsKey(id))
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			typeData.remove(id);
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
	}
}
