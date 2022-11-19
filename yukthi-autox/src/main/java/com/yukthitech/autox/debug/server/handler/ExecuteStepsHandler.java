package com.yukthitech.autox.debug.server.handler;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.common.ExecuteStepsClientMssg;
import com.yukthitech.autox.test.CustomUiLocator;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.IEntryPoint;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class ExecuteStepsHandler extends AbstractServerDataHandler<ExecuteStepsClientMssg> implements IEntryPoint
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

	private AutomationContext automationContext;
	
	private TestSuite testSuite = new TestSuite("[dynamic-test-case]");
	
	private TestCase testCase = new TestCase("[dynamic-tst-suite]");
	
	public ExecuteStepsHandler(AutomationContext automationContext)
	{
		super(ExecuteStepsClientMssg.class);
		this.automationContext = automationContext;
	}

	@Override
	public boolean processData(ExecuteStepsClientMssg steps)
	{
		/*
		if(!automationContext.isReadyToInteract())
		{
			logger.warn("As the server is not yet ready to interact, interactive steps send to server are ignored.");
			return false;
		}
		
		StepHolder stepHolder = parseSteps(steps.getStepsToExecute());
		
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

		if(CollectionUtils.isEmpty(stepHolder.getSteps()))
		{
			return true;
		}
		
		executeSteps(stepHolder.getSteps());
		*/
		return true;
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
			XMLBeanParser.parse(bis, stepHolder, automationContext.getTestSuiteParserHandler());
			
			return stepHolder;
		} catch(Exception ex)
		{
			logger.error("Failed to parse step list from interactive step xml:\n", xml, ex);
			return null;
		}
	}
	
	private void executeSteps(List<IStep> steps)
	{
		/*
		if(automationContext.getActiveTestSuite() != null)
		{
			automationContext.setActiveTestSuite(automationContext.getActiveTestSuite());
		}
		else
		{
			automationContext.setActiveTestSuite(testSuite);
		}
		
		if(automationContext.getActiveTestCase() != null)
		{
			automationContext.setActiveTestCase(automationContext.getActiveTestCase(), null);
		}
		else
		{
			automationContext.setActiveTestCase(testCase, null);
		}
		
		//automationContext.getAutomationExecutor().newSteps("Dynamic-steps", this, steps);
	
	*/
	}
	
	@Override
	public String toText()
	{
		return "[Interactive]";
	}
}
