package com.yukthitech.autox.monitor;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.event.EventListenerManager;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Client to get monitor data.
 * @author akiran
 */
public class MonitorClient
{
	private static Logger logger = LogManager.getLogger(MonitorClient.class);
	
	private static AtomicInteger counter = new AtomicInteger(); 
	
	/**
	 * Gap time at which server connection should be checked.
	 */
	private static final int GAP_TIME = 500;
	
	/**
	 * Maximum time for which client should wait for server.
	 */
	private static final int MAX_WAIT_TIME = 60000;
	
	/**
	 * Server host on which server is running.
	 */
	private String serverHost;
	
	/**
	 * Port where server is expected to run.
	 */
	private int serverPort;
	
	/**
	 * Socket connected to server.
	 */
	private Socket clientSocket;
	
	/**
	 * Thread to read data from server.
	 */
	private Thread readerThread;
	
	/**
	 * Stream for which server data can be read.
	 */
	private ObjectInputStream readerStream;
	
	/**
	 * Buffer to maintain read data. Till it is sent to listeners.
	 */
	private LinkedList<Serializable> readBuffer = new LinkedList<>();
	
	/**
	 * Manager to manage listeners.
	 */
	private EventListenerManager<IAsyncClientDataHandler> listenerManager = EventListenerManager.newEventListenerManager(IAsyncClientDataHandler.class, false);
	
	/**
	 * Thread to invoke listeners.
	 */
	private Thread listenerThread;
	
	private MonitorClient(String serverHost, int serverPort)
	{
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		
		readerThread = new Thread(this::readDataFromServer, "Monitor Client Reader - " + counter.incrementAndGet());
		listenerThread = new Thread(this::invokeListeners, "Monitor Client Listeners - " + counter.incrementAndGet());
	}
	
	/**
	 * Method to read data from server and add it to read buffer.
	 */
	@SuppressWarnings("unchecked")
	private void readDataFromServer()
	{
		//wait till client socket is closed
		while(clientSocket != null && !clientSocket.isClosed())
		{
			try
			{
				List<Serializable> dataLst = (List<Serializable>) readerStream.readObject();
				
				synchronized(this)
				{
					this.readBuffer.addAll(dataLst);
					super.notifyAll();
				}
			}catch(Exception ex)
			{
				logger.error("An error occurred while fetching data from server", ex);
			}
		}
	}
	
	/**
	 * Adds specified handler to this client listener list.
	 * @param handler
	 */
	public void addAsyncClientDataHandler(IAsyncClientDataHandler handler)
	{
		this.listenerManager.addListener(handler);
	}
	
	/**
	 * Thread method to invoke listeners.
	 */
	private void invokeListeners()
	{
		while(clientSocket != null && !clientSocket.isClosed())
		{
			List<Serializable> data = null;
			
			synchronized(this)
			{
				if(this.readBuffer.isEmpty())
				{
					//wait till data is available
					try
					{
						super.wait();
					}catch(Exception ex)
					{}
				}
				
				data = new ArrayList<>(this.readBuffer);
				this.readBuffer.clear();
			}
			
			for(Serializable obj : data)
			{
				this.listenerManager.get().processData(obj);
			}
		}
	}
	
	/**
	 * Starts the client which will wait till is connected to server.
	 */
	private void start()
	{
		logger.debug("Waiting for server to be up and running...");
		
		long startTime = System.currentTimeMillis();
		long diff = 0;
		Exception lastEx = null;
		
		while(diff < MAX_WAIT_TIME)
		{
			try
			{
				clientSocket = new Socket(serverHost, serverPort);
				
				InputStream is = clientSocket.getInputStream();
				this.readerStream = new ObjectInputStream(is);
				break;
			}catch(Exception ex)
			{
				clientSocket  = null;
				lastEx = ex;
			}
			
			try
			{
				Thread.sleep(GAP_TIME);
			}catch(Exception ex)
			{}

			diff = System.currentTimeMillis() - startTime;
		}

		if(clientSocket == null)
		{
			throw new InvalidStateException("Failed to connect to server. Last exception while connecting to server was: {}", "" + lastEx);
		}
		
		logger.debug("Successfully connected to server...");
		readerThread.start();
		listenerThread.start();
	}
	
	public static MonitorClient startClient(String serverHost, int serverPort)
	{
		MonitorClient client = new MonitorClient(serverHost, serverPort);
		client.start();
		
		return client;
	}
}
