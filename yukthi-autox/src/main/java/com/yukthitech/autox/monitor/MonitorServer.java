package com.yukthitech.autox.monitor;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manager to send manage monitoring information and communicate with monitoring client.
 * @author akiran
 */
public class MonitorServer
{
	private static Logger logger = LogManager.getLogger(MonitorServer.class);
	
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
	private ObjectOutputStream clientStream;
	
	/**
	 * lock to be used to sync writing on client stream.
	 */
	private ReentrantLock clientStreamLock = new ReentrantLock();
	
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
	
	private MonitorServer(int serverPort)
	{
		this.serverPort = serverPort;
		
		writeThread = new Thread(this::sendDataToClient, "Monitor Writer");
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
			
			clientStreamLock.lock();
			
			try
			{
				clientStream.writeObject(dataBuff);
				clientStream.flush();
			}catch(Exception ex)
			{
				logger.error("An error occurred while sending data to client. There might be some data loss being sent to client", ex);
			}finally
			{
				clientStreamLock.unlock();
			}
		}
	}
	
	/**
	 * Starts the manager and waits for clients to get connected.
	 */
	private void start()
	{
		try
		{
			serverSocket = new ServerSocket(serverPort);
			logger.info("Waiting for client to connect. Monitor port: {}", serverPort);
			
			clientSocket = serverSocket.accept();
			OutputStream outputStream = clientSocket.getOutputStream();
			clientStream = new ObjectOutputStream(outputStream);
	
			logger.debug("Client got connected...");
			writeThread.start();
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
	public static MonitorServer startManager(int port)
	{
		if(port <= 0)
		{
			throw new InvalidArgumentException("Invalid monitor port specified: " + port);
		}
		
		MonitorServer monitorManager = new MonitorServer(port);
		monitorManager.start();
		
		return monitorManager;
	}
	
	/**
	 * Used to send monitoring data to the client.
	 * @param data data to be sent.
	 */
	public void sendAsync(Serializable data)
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
}
