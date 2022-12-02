package com.yukthitech.autox.debug.server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.debug.common.ClientMssgDebuggerInit;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.debug.server.DebugServer;

/**
 * Used to execute test case fully/partially in interactive environment.
 * @author akiran
 */
public class DebuggerInitHandler extends AbstractServerDataHandler<ClientMssgDebuggerInit>
{
	private static Logger logger = LogManager.getLogger(DebuggerInitHandler.class);
	
	/**
	 * Flag indicating if init message is processed or not.
	 */
	private static boolean initialized = false;

	public DebuggerInitHandler()
	{
		super(ClientMssgDebuggerInit.class);
	}
	
	public static boolean isInitialized()
	{
		return initialized;
	}

	@Override
	public void processData(ClientMssgDebuggerInit data)
	{
		logger.debug("Executing interactive test case command: {}", data);
		
		DebugFlowManager.getInstance().setDebugPoints(data.getDebugPoints());
		initialized = true;
		
		DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(data.getRequestId(), true, null));
	}
}
