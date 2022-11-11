package com.yukthitech.autox.debug.server;

import java.io.Serializable;

import com.yukthitech.autox.debug.common.MessageConfirmationServerMssg;
import com.yukthitech.autox.debug.common.MessageWrapper;
import com.yukthitech.autox.debug.server.handler.AbstractServerDataHandler;

/**
 * Wrapper around data handler that is used to perform pre and post operations
 * of message handling.
 * @author akiran
 */
public class ServerDataHandlerWrapper extends AbstractServerDataHandler<Serializable>
{
	/**
	 * Server on which this handler is registered.
	 */
	private DebugServer monitorServer;
	
	/**
	 * Actual data handler which this wrapper is wrapping.
	 */
	private IServerDataHandler<Serializable> actualDataHandler;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ServerDataHandlerWrapper(DebugServer monitorServer, IServerDataHandler<? extends Serializable> actualDataHandler)
	{
		super(Serializable.class);
		
		this.monitorServer = monitorServer;
		this.actualDataHandler = (IServerDataHandler) actualDataHandler;
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
			monitorServer.sendClientMessage(new MessageConfirmationServerMssg(wrapper.getId(), successful, error));
		}
		
		return res;
	}
}
