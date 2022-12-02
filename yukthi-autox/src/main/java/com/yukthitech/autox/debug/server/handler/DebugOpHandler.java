package com.yukthitech.autox.debug.server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.debug.common.ClientMssgDebugOp;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.debug.server.DebugServer;
import com.yukthitech.autox.debug.server.LiveDebugPoint;

/**
 * Used to update debug points.
 * @author akiran
 */
public class DebugOpHandler extends AbstractServerDataHandler<ClientMssgDebugOp>
{
	private static Logger logger = LogManager.getLogger(DebugOpHandler.class);
	
	public DebugOpHandler()
	{
		super(ClientMssgDebugOp.class);
	}
	
	@Override
	public void processData(ClientMssgDebugOp data)
	{
		logger.debug("Executing debug OP command: {}", data);
		
		LiveDebugPoint livePoint = DebugFlowManager.getInstance().getLiveDebugPoint(data.getExecutionId());
		
		if(livePoint == null)
		{
			DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(data.getRequestId(), false, "No live-debug-point found with execution id: " + data.getExecutionId()));
			return;
		}
		
		boolean res = livePoint.release(data.getExecutionId(), data.getDebugOp());
		
		if(res)
		{
			DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(data.getRequestId(), true, null));
		}
	}
}
