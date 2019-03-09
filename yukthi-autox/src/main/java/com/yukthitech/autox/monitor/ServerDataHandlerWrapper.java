package com.yukthitech.autox.monitor;

import java.io.Serializable;

import com.yukthitech.autox.monitor.ienv.MessageConfirmation;

/**
 * Wrapper around data handler that is used to perform pre and post operations
 * of message handling.
 * @author akiran
 */
public class ServerDataHandlerWrapper implements IAsyncServerDataHandler
{
	/**
	 * Server on which this handler is registered.
	 */
	private MonitorServer monitorServer;
	
	/**
	 * Actual data handler which this wrapper is wrapping.
	 */
	private IAsyncServerDataHandler actualDataHandler;
	
	public ServerDataHandlerWrapper(MonitorServer monitorServer, IAsyncServerDataHandler actualDataHandler)
	{
		this.monitorServer = monitorServer;
		this.actualDataHandler = actualDataHandler;
	}

	@Override
	public boolean processData(Serializable data)
	{
		String error = null;
		MessageWrapper wrapper = (data instanceof MessageWrapper) ? (MessageWrapper) data : null;
		Serializable actualMessage = wrapper != null ? wrapper.getMessage() : data;
		
		boolean res = false;
		
		try
		{
			res = actualDataHandler.processData(actualMessage);
		}catch(Exception ex)
		{
			res = true;
			error = "Error: " + ex;
		}
		
		if(res && wrapper != null && wrapper.isConfirmationRequired())
		{
			boolean successful = (error == null);
			monitorServer.sendAsync(new MessageConfirmation(wrapper.getId(), successful, error));
		}
		
		return res;
	}
}
