package com.yukthitech.autox.debug.server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.common.DebuggerInitClientMssg;
import com.yukthitech.autox.debug.server.DebugFlowManager;

/**
 * Used to execute test case fully/partially in interactive environment.
 * @author akiran
 */
public class DebuggerInitHandler extends AbstractServerDataHandler<DebuggerInitClientMssg>
{
	private static Logger logger = LogManager.getLogger(DebuggerInitHandler.class);
	
	/**
	 * Flag indicating if init message is processed or not.
	 */
	private static boolean initialized = false;

	public DebuggerInitHandler(AutomationContext automationContext)
	{
		super(DebuggerInitClientMssg.class);
	}
	
	public static boolean isInitialized()
	{
		return initialized;
	}

	@Override
	public boolean processData(DebuggerInitClientMssg data)
	{
		logger.debug("Executing interactive test case command: {}", data);
		
		DebugFlowManager.getInstance().addDebugPoints(data.getDebugPoints());
		initialized = true;
		return true;
	}
}
