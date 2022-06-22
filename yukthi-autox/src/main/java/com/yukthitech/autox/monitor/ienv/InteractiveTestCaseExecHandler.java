package com.yukthitech.autox.monitor.ienv;

import java.io.File;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.InteractiveEnvironmentContext;
import com.yukthitech.autox.InteractiveExecutionController;
import com.yukthitech.autox.monitor.IAsyncServerDataHandler;

/**
 * Used to execute test case fully/partially in interactive environment.
 * @author akiran
 */
public class InteractiveTestCaseExecHandler implements IAsyncServerDataHandler
{
	private static Logger logger = LogManager.getLogger(InteractiveTestCaseExecHandler.class);

	private AutomationContext automationContext;
	
	public InteractiveTestCaseExecHandler(AutomationContext automationContext)
	{
		this.automationContext = automationContext;
	}

	@Override
	public boolean processData(Serializable data)
	{
		if(!(data instanceof InteractiveTestCaseExecDetails))
		{
			return false;
		}
		
		logger.debug("Executing interactive test case command: {}", data);
		
		if(!automationContext.isReadyToInteract())
		{
			logger.warn("As the server is not yet ready to interact, interactive steps send to server are ignored.");
			return false;
		}
		
		InteractiveTestCaseExecDetails details = (InteractiveTestCaseExecDetails) data;
		
		InteractiveExecutionController executionController = new InteractiveExecutionController();
		executionController.addEndPoint(new File(details.getFilePath()), details.getLineNumber());
		
		InteractiveEnvironmentContext interactiveEnvironmentContext = automationContext.getInteractiveEnvironmentContext();
		interactiveEnvironmentContext.setExecutionController(executionController);
		interactiveEnvironmentContext.setExecuteGlobalSetup(false);
		
		try
		{
			logger.debug("Executing by restrictng execution to test case: {}", details.getTestCase());
			automationContext.getBasicArguments().setTestCases(details.getTestCase());
			automationContext.getAutomationExecutor().pushTestSuiteGroup(interactiveEnvironmentContext.getTestSuiteGroup());
		}finally
		{
			interactiveEnvironmentContext.setExecutionController(null);
		}
		
		return true;
	}
}
