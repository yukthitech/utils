package com.yukthitech.test;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class TestServer
{
	public static Server start(String[] args) throws Exception
	{
		Server server = new Server(8080);
		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "index.html" });
		resource_handler.setResourceBase("./src/test/resources/web");

		WebAppContext webapp1 = new WebAppContext();
		webapp1.setResourceBase("./src/test/resources/app");
		webapp1.setContextPath("/app");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { webapp1, resource_handler, new DefaultHandler() });
		server.setHandler(handlers);

		server.start();
		System.out.println("Server started at 8080...");
		return server;
	}

	public static void main(String[] args) throws Exception
	{
		Server server = start(args);
		server.join();
	}
}
