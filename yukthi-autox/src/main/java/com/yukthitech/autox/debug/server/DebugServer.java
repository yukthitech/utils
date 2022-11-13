package com.yukthitech.autox.debug.server;

import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.debug.common.DebugPoint;
import com.yukthitech.autox.debug.common.ExecutionPausedServerMssg;
import com.yukthitech.autox.debug.common.MessageWrapper;
import com.yukthitech.autox.debug.server.handler.DebuggerInitHandler;
import com.yukthitech.autox.debug.server.handler.ExecuteStepsHandler;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manager to send manage monitoring information and communicate with monitoring client.
 * @author akiran
 */
public class DebugServer
{
	private static Logger logger = LogManager.getLogger(DebugServer.class);
	
	/**
	 * System property usng which monitoring will be enabled on specified port.
	 */
	public static final String SYS_PROP_MONITOR_PORT = "autox.monitor.port";
	
	/**
	 * Port on which monitoring manager should run.
	 */
	private int serverPort;
	
	/**
	 * Server socket.
	 */
	private ServerSocket serverSocket;
	
	/**
	 * Client socket.
	 */
	private Socket clientSocket;
	
	/**
	 * Stream to send data to client.
	 */
	private ObjectOutputStream clientOutputStream;
	
	/**
	 * Client stream to read objects from client.
	 */
	private ObjectInputStream clientInputStream;
	
	/**
	 * Data buffer to be sent to the client.
	 */
	private LinkedList<Serializable> clientDataBuffer = new LinkedList<>();
	
	/**
	 * Lock to synchronize on client data buffer.
	 */
	private ReentrantLock clientDataBufferLock = new ReentrantLock();
	
	/**
	 * Thread to send data to client.
	 */
	private Thread writeThread;
	
	/**
	 * Thread to read data from client.
	 */
	private Thread readThread;
	
	/**
	 * Manager to manage listeners.
	 */
	private Map<Class<?>, IServerDataHandler<Serializable>> dataHandlers = new HashMap<>();
	
	private DebugServer(int serverPort)
	{
		this.serverPort = serverPort;
		
		writeThread = new Thread(this::sendDataToClient, "Debug Writer");
		readThread = new Thread(this::readDataFromClient, "Debug Reader");
		
		addAsyncServerDataHandler( wrap(new ExecuteStepsHandler(AutomationContext.getInstance())) );
		addAsyncServerDataHandler( wrap(new DebuggerInitHandler(AutomationContext.getInstance())) );
	}
	
	private IServerDataHandler<Serializable> wrap(IServerDataHandler<? extends Serializable> handler)
	{
		return new ServerDataHandlerWrapper(this, handler);
	}
	
	/**
	 * Adds the listener to the server.
	 * @param handler handler to add
	 */
	public void addAsyncServerDataHandler(IServerDataHandler<Serializable> handler)
	{
		this.dataHandlers.put(handler.getClass(), handler);
	}
	
	private void sendDataToClient()
	{
		List<Serializable> dataBuff = null;
		
		while(true)
		{
			clientDataBufferLock.lock();
			
			try
			{
				if(clientDataBuffer.isEmpty())
				{
					continue;
				}
				
				dataBuff = new ArrayList<>(clientDataBuffer);
				clientDataBuffer.clear();
			}finally
			{
				clientDataBufferLock.unlock();
			}

			try
			{
				try
				{
					clientOutputStream.writeObject(dataBuff);
				}catch(NotSerializableException ex)
				{
					clientOutputStream.writeObject("<< Not serializable >>");
				}
				
				clientOutputStream.flush();
			} catch(Exception ex)
			{
				logger.error("An error occurred while sending data to client. There might be some data loss being sent to client", ex);
			}
		}
	}
	
	/**
	 * Reads data from client.
	 */
	private void readDataFromClient()
	{
		//wait till client socket is closed
		while(clientSocket != null && !clientSocket.isClosed())
		{
			try
			{
				Serializable object = (Serializable) clientInputStream.readObject();
				logger.debug("Received command from client: {}", object);
				
				Class<?> dataType = null;
				
				if(object instanceof MessageWrapper)
				{
					dataType = ((MessageWrapper) object).getMessage().getClass();
				}
				else
				{
					dataType = object.getClass();
				}
				
				IServerDataHandler<Serializable> handler = this.dataHandlers.get(dataType);
				
				if(handler != null)
				{
					handler.processData(object);
				}
				else
				{
					logger.warn("Unsupported debug-message received: {}", object);
				}
			}catch(Exception ex)
			{
				logger.error("An error occurred while fetching data from client", ex);
				
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
	 * Starts the manager and waits for clients to get connected.
	 */
	private void start()
	{
		logger.debug("Starting debug-server on port: {}", serverPort);
		
		try
		{
			serverSocket = new ServerSocket(serverPort);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to start debug server on port: " + serverPort, ex);
		}
		
		try
		{
			logger.info("Waiting for client to connect. Monitor port: {}", serverPort);
			
			clientSocket = serverSocket.accept();
			OutputStream outputStream = clientSocket.getOutputStream();
			
			clientOutputStream = new ObjectOutputStream(outputStream);
			clientInputStream = new ObjectInputStream(clientSocket.getInputStream());
	
			logger.debug("Client got connected...");
			writeThread.start();
			readThread.start();
			
			logger.info("Waiting for client to send init info...");
			
			//wait till init message is received
			while(!DebuggerInitHandler.isInitialized())
			{
				//wait for 100 millis and check again
				AutomationUtils.sleep(100);
			}
			
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while waiting for client to connect.", ex);
		}
	}
	
	/**
	 * Starts the manager and makes current thread to wait till client connects.
	 * @param port
	 * @return
	 */
	public static DebugServer start(int port)
	{
		if(port <= 0)
		{
			throw new InvalidArgumentException("Invalid monitor port specified: " + port);
		}
		
		DebugServer monitorManager = new DebugServer(port);
		monitorManager.start();
		
		return monitorManager;
	}
	
	/**
	 * Used to send monitoring data to the client.
	 * @param data data to be sent.
	 */
	public void sendClientMessage(Serializable data)
	{
		clientDataBufferLock.lock();
		
		try
		{
			clientDataBuffer.add(data);
		}finally
		{
			clientDataBufferLock.unlock();
		}
	}
	
	void executionPaused(AutomationContext context, DebugPoint debugPoint)
	{
		List<ExecutionPausedServerMssg.StackElement> stackTrace = context.getExecutionStack()
			.getStackTrace()
			.stream()
			.map(elem -> new ExecutionPausedServerMssg.StackElement(elem.getLocation(), elem.getLineNumber()))
			.collect(Collectors.toList());
		
		ExecutionPausedServerMssg pauseMssg = new ExecutionPausedServerMssg(debugPoint.getFilePath(), debugPoint.getLineNumber(), stackTrace);
		sendClientMessage(pauseMssg);
	}
}
