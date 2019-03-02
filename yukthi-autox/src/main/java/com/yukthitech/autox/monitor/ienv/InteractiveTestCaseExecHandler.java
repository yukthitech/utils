package com.yukthitech.autox.monitor.ienv;

import java.io.File;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.InteractiveEnvironmentContext;
import com.yukthitech.autox.InteractiveExecutionController;
import com.yukthitech.autox.monitor.IAsyncServerDataHandler;
import com.yukthitech.autox.test.TestSuiteExecutor;

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
	public void processData(Serializable data)
	{
		if(!(data instanceof InteractiveTestCaseExecDetails))
		{
			return;
		}
		
		logger.debug("Executing interactive test case command: {}", data);
		
		if(!automationContext.isReadyToInteract())
		{
			logger.warn("As the server is not yet ready to interact, interactive steps send to server are ignored.");
			return;
		}
		
		InteractiveTestCaseExecDetails details = (InteractiveTestCaseExecDetails) data;
		
		InteractiveExecutionController executionController = new InteractiveExecutionController();
		executionController.addEndPoint(new File(details.getFilePath()), details.getLineNumber());
		
		InteractiveEnvironmentContext interactiveEnvironmentContext = automationContext.getInteractiveEnvironmentContext();
		interactiveEnvironmentContext.setExecutionController(executionController);
		interactiveEnvironmentContext.setExecuteGlobalSetup(false);
		
		try
		{
			TestSuiteExecutor testSuiteExecutor = new TestSuiteExecutor(automationContext, interactiveEnvironmentContext.getTestSuiteGroup());
			automationContext.getBasicArguments().setTestCases(details.getTestCase());
			
			logger.debug("Executing by restrictng execution to test case: {}", details.getTestCase());
			testSuiteExecutor.executeTestSuites();
		}finally
		{
			interactiveEnvironmentContext.setExecutionController(null);
		}
	}
}
