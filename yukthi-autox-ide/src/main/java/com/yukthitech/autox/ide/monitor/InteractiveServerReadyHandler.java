package com.yukthitech.autox.ide.monitor;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.monitor.IAsyncClientDataHandler;
import com.yukthitech.autox.monitor.ienv.InteractiveServerReady;

/**
 * Handler which would set interactive ready flag based on message received from server.
 * @author akiran
 */
public class InteractiveServerReadyHandler implements IAsyncClientDataHandler
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
		if(!(data instanceof InteractiveServerReady))
		{
			return;
		}
	
		logger.debug("Received ready to interact message from server for environment: {}", executionEnvironment.getName());
		executionEnvironment.setReadyToInteract(true);
	}
}
