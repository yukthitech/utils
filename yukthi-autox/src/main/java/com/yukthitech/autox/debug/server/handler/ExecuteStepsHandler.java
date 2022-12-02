package com.yukthitech.autox.debug.server.handler;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.common.ClientMssgExecuteSteps;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.server.DebugFlowManager;
import com.yukthitech.autox.debug.server.DebugServer;
import com.yukthitech.autox.debug.server.LiveDebugPoint;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class ExecuteStepsHandler extends AbstractServerDataHandler<ClientMssgExecuteSteps>
{
	private static Logger logger = LogManager.getLogger(ExecuteStepsHandler.class);

	private static String stepHolderTemplate;
	
	static
	{
		try
		{
			stepHolderTemplate = IOUtils.toString(ExecuteStepsHandler.class.getResourceAsStream("/step-holder-template.xml"), Charset.defaultCharset());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading resource: /step-holder-template.xml", ex);
		}
	}

	public ExecuteStepsHandler()
	{
		super(ClientMssgExecuteSteps.class);
	}

	@Override
	public void processData(ClientMssgExecuteSteps steps)
	{
		LiveDebugPoint livepoint = DebugFlowManager.getInstance().getLiveDebugPoint(steps.getExecutionId());
		
		if(livepoint == null)
		{
			logger.warn("Invalid execution id specified: " + steps.getExecutionId());
			DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(steps.getRequestId(), false, "Invalid live point id specified: %s", steps.getExecutionId()));
			return;
		}
		
		StepHolder stepHolder = parseSteps(steps.getStepsToExecute());
		
		/*
		if(CollectionUtils.isNotEmpty(stepHolder.getCustomUiLocators()))
		{
			for(CustomUiLocator customUiLocator : stepHolder.getCustomUiLocators())
			{
				logger.debug("Reloading custom-ui-locator: {}", customUiLocator.getName());
				
				automationContext.addOrReplaceCustomUiLocator(customUiLocator);
			}
		}
		
		
		if(CollectionUtils.isNotEmpty(stepHolder.getFunctions()))
		{
			TestSuite activeTestSuite = automationContext.getActiveTestSuite();

			for(Function func : stepHolder.getFunctions())
			{
				logger.debug("Reloading function: {}", func.getName());
				
				activeTestSuite.addOrReplaceFunction(func);
			}
		}
		*/

		if(CollectionUtils.isEmpty(stepHolder.getSteps()))
		{
			DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(steps.getRequestId(), false, "No steps found to execute: %s", steps.getExecutionId()));
			return;
		}
		
		livepoint.executeSteps(steps.getRequestId(), stepHolder.getSteps());
	}
	
	private StepHolder parseSteps(String xml)
	{
		StepHolder stepHolder = new StepHolder();
		
		String stepXml = CommonUtils.replaceExpressions(
				CommonUtils.toMap("steps", xml), 
				stepHolderTemplate, null);
		
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(stepXml.getBytes());
			XMLBeanParser.parse(bis, stepHolder, AutomationContext.getInstance().getTestSuiteParserHandler());
			
			return stepHolder;
		} catch(Exception ex)
		{
			logger.error("Failed to parse step list from interactive step xml:\n", xml, ex);
			return null;
		}
	}
}
