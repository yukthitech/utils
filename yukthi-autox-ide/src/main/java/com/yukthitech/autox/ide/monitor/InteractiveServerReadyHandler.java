package com.yukthitech.autox.ide.monitor;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.debug.client.IClientDataHandler;
import com.yukthitech.autox.debug.common.InteractiveServerReadyServerMssg;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;

/**
 * Handler which would set interactive ready flag based on message received from server.
 * @author akiran
 */
public class InteractiveServerReadyHandler implements IClientDataHandler
{
	private static Logger logger = LogManager.getLogger(InteractiveServerReadyHandler.class);
	
	private ExecutionEnvironment executionEnvironment;
	
	public InteractiveServerReadyHandler(ExecutionEnvironment executionEnvironment)
	{
		this.executionEnvironment = executionEnvironment;
	}

	@Override
	public void processData(Serializable data)
	{
		if(!(data instanceof InteractiveServerReadyServerMssg))
		{
			return;
		}
	
		logger.debug("Received ready to interact message from server for environment: {}", executionEnvironment.getName());
		executionEnvironment.setReadyToInteract(true);
	}
}
