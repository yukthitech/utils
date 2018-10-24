package com.yukthitech.autox.monitor.ienv;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.monitor.IAsyncServerDataHandler;
import com.yukthitech.autox.test.StepExecutor;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class InteractiveStepHandler implements IAsyncServerDataHandler
{
	private static Logger logger = LogManager.getLogger(InteractiveStepHandler.class);

	private static String stepHolderTemplate;
	
	static
	{
		try
		{
			stepHolderTemplate = IOUtils.toString(InteractiveStepHandler.class.getResourceAsStream("/step-holder-template.xml"));
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading resource: /step-holder-template.xml", ex);
		}
	}

	private AutomationContext automationContext;
	
	private ExecutionLogger executionLogger;
	
	private TestSuite testSuite = new TestSuite("[dynamic-test-case]");
	
	private TestCase testCase = new TestCase("[dynamic-tst-suite]");
	
	public InteractiveStepHandler(AutomationContext automationContext)
	{
		this.automationContext = automationContext;
		executionLogger = new ExecutionLogger(automationContext, "[dynamic]", "[dynamic]");
	}

	@Override
	public void processData(Serializable data)
	{
		if(!(data instanceof InteractiveExecuteSteps))
		{
			return;
		}
		
		if(!automationContext.isReadyToInteract())
		{
			logger.warn("As the server is not yet ready to interact, interactive steps send to server are ignored.");
			return;
		}
		
		InteractiveExecuteSteps steps = (InteractiveExecuteSteps) data;
		List<IStep> stepsToExe = parseSteps(steps.getStepsToExecute());
		
		if(stepsToExe == null)
		{
			return;
		}
		
		executeSteps(stepsToExe);
	}
	
	private List<IStep> parseSteps(String xml)
	{
		StepHolder stepHolder = new StepHolder();
		
		String stepXml = CommonUtils.replaceExpressions(
				CommonUtils.toMap("steps", xml), 
				stepHolderTemplate, null);
		
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(stepXml.getBytes());
			XMLBeanParser.parse(bis, stepHolder, automationContext.getTestSuiteParserHandler());
			
			return stepHolder.getSteps();
		} catch(Exception ex)
		{
			logger.error("Failed to parse step list from interactive step xml:\n", xml, ex);
			return null;
		}
	}
	
	private void executeSteps(List<IStep> steps)
	{
		automationContext.setActiveTestSuite(testSuite);
		automationContext.setActiveTestCase(testCase, null);
		
		for(IStep step : steps)
		{
			try
			{
				StepExecutor.executeStep(automationContext, executionLogger, step);
			} catch(Exception ex)
			{
				logger.error("An error occurred while executing interactive step: " + step, ex);
				
				StepExecutor.handleException(automationContext, testCase, step, executionLogger, ex, null);
				break;
			}
		}
	}
}
