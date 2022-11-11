package com.yukthitech.autox.debug.client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.debug.common.DebuggerInitClientMssg;
import com.yukthitech.autox.debug.common.MessageConfirmationServerMssg;
import com.yukthitech.autox.debug.common.MessageWrapper;
import com.yukthitech.utils.event.EventListenerManager;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Client to get monitor data.
 * @author akiran
 */
public class DebugClient
{
	private static Logger logger = LogManager.getLogger(DebugClient.class);
	
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
	 * Stream to write data to server.
	 */
	private ObjectOutputStream writerStream;
	
	/**
	 * Buffer to maintain read data. Till it is sent to listeners.
	 */
	private LinkedList<Serializable> readBuffer = new LinkedList<>();
	
	/**
	 * Manager to manage listeners.
	 */
	private EventListenerManager<IClientDataHandler> listenerManager = EventListenerManager.newEventListenerManager(IClientDataHandler.class, false);
	
	/**
	 * Thread to invoke listeners.
	 */
	private Thread listenerThread;
	
	/**
	 * Callback methods for messages.
	 */
	private Map<String, IMessageCallback> idToCallback = new HashMap<>();
	
	/**
	 * Lock to be used for callbacks.
	 */
	private ReentrantLock callbackLock = new ReentrantLock();
	
	private DebugClient(String serverHost, int serverPort)
	{
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		
		readerThread = new Thread(this::readDataFromServer, "Debug Client Reader - " + counter.incrementAndGet());
		listenerThread = new Thread(this::invokeListeners, "Debug Client Listeners - " + counter.incrementAndGet());
	}
	
	/**
	 * Method to read data from server and add it to read buffer.
	 */
	@SuppressWarnings("unchecked")
	private void readDataFromServer()
	{
		boolean firstError = true;
		
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
				if(ex instanceof SocketException)
				{
					//ignore first erorr as it may happen during env termination
					if(!firstError)
					{
						logger.error("An error occurred while fetching data from server", ex);	
					}
					else
					{
						firstError = false;
					}
				}
				
				//as the exception might be because of client close. So wait for second and check again
				try
				{
					Thread.sleep(1000);
				}catch(Exception e1)
				{}
			}
		}
	}
	
	/**
	 * Adds specified handler to this client listener list.
	 * @param handler
	 */
	public void addAsyncClientDataHandler(IClientDataHandler handler)
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
				if(obj instanceof MessageConfirmationServerMssg)
				{
					MessageConfirmationServerMssg confirm = (MessageConfirmationServerMssg) obj;
					handleConfirmMessage(confirm);
					continue;
				}
				
				this.listenerManager.get().processData(obj);
			}
		}
	}
	
	private void handleConfirmMessage(MessageConfirmationServerMssg mssg)
	{
		logger.debug("For message with id {} got confirmation", mssg.getRequestId());
		IMessageCallback callback = null;
		
		callbackLock.lock();
		
		try
		{
			callback = idToCallback.remove(mssg.getRequestId());
		}finally
		{
			callbackLock.unlock();
		}
		
		if(callback == null)
		{
			return;
		}
		
		try
		{
			logger.debug("For message with id {} invoking the callback", mssg.getRequestId());
			callback.onProcess(mssg);
		}catch(Exception ex)
		{
			logger.error("An error occurred while processing confirmation message: {}", mssg, ex);
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
				this.writerStream = new ObjectOutputStream(clientSocket.getOutputStream());
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
	
	public static DebugClient startClient(String serverHost, int serverPort, DebuggerInitClientMssg initMssg)
	{
		DebugClient client = new DebugClient(serverHost, serverPort);
		client.start();
		
		client.sendDataToServer(initMssg);
		return client;
	}
	
	public void sendDataToServer(Serializable data)
	{
		sendDataToServer(data, null);
	}
	
	public void sendDataToServer(Serializable data, IMessageCallback callback)
	{
		if(clientSocket == null)
		{
			throw new InvalidStateException("Client process is disconnected");
		}
		
		try
		{
			if(callback != null)
			{
				MessageWrapper wrapper = new MessageWrapper(data, true);
				logger.debug("For message {} generated id: {}", data, wrapper.getId());
				
				this.writerStream.writeObject(wrapper);
				
				callbackLock.lock();
				
				try
				{
					idToCallback.put(wrapper.getId(), callback);
				}finally
				{
					callbackLock.unlock();
				}
			}
			else
			{
				this.writerStream.writeObject(data);
			}
			
			this.writerStream.flush();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while sending data to server.", ex);
		}
	}
	
	/**
	 * Called when process is terminated.
	 */
	public void processTerminated()
	{
		callbackLock.lock();
		
		try
		{
			Set<String> callbackIds = new HashSet<>(idToCallback.keySet());
			
			for(String id : callbackIds)
			{
				IMessageCallback callback = idToCallback.remove(id);
				callback.terminated();
			}
		}finally
		{
			callbackLock.unlock();
		}
	}
	
	public void close()
	{
		if(clientSocket == null)
		{
			return;
		}
		
		try
		{
			clientSocket.close();
		}catch(Exception ex)
		{
			logger.error("An error occurred while closing monitoring client. Exception: " + ex);
		}
		
		clientSocket = null;
	}
}
