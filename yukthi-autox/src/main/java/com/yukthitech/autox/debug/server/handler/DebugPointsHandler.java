package com.yukthitech.autox.debug.server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.debug.common.ClientMssgDebugPoints;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.debug.server.DebugServer;

/**
 * Used to update debug points.
 * @author akiran
 */
public class DebugPointsHandler extends AbstractServerDataHandler<ClientMssgDebugPoints>
{
	private static Logger logger = LogManager.getLogger(DebugPointsHandler.class);
	
	public DebugPointsHandler()
	{
		super(ClientMssgDebugPoints.class);
	}
	
	@Override
	public void processData(ClientMssgDebugPoints data)
	{
		logger.debug("Executing debug points command: {}", data);
		
		DebugFlowManager.getInstance().setDebugPoints(data.getDebugPoints());
		DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(data.getRequestId(), true, null));
	}
}
