package com.yukthitech.autox.debug.server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.debug.common.ClientMssgEvalExpression;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.debug.server.DebugServer;
import com.yukthitech.autox.debug.server.LiveDebugPoint;

public class EvalExpressionHandler extends AbstractServerDataHandler<ClientMssgEvalExpression>
{
	private static Logger logger = LogManager.getLogger(EvalExpressionHandler.class);

	public EvalExpressionHandler()
	{
		super(ClientMssgEvalExpression.class);
	}

	@Override
	public void processData(ClientMssgEvalExpression evalExpr)
	{
		LiveDebugPoint livepoint = DebugFlowManager.getInstance().getLiveDebugPoint(evalExpr.getExecutionId());
		
		if(livepoint == null)
		{
			logger.warn("Invalid live point id specified: " + evalExpr.getExecutionId());
			DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(evalExpr.getRequestId(), false, "Invalid live point id specified: %s", evalExpr.getExecutionId()));
			return;
		}
		
		livepoint.evalExpression(evalExpr.getRequestId(), evalExpr.getExpression());
	}
}
