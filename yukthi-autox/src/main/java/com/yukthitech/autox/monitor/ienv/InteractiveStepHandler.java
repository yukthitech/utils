package com.yukthitech.autox.monitor.ienv;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.InteractiveEnvironmentContext;
import com.yukthitech.autox.monitor.IAsyncServerDataHandler;
import com.yukthitech.autox.test.CustomUiLocator;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.IEntryPoint;
import com.yukthitech.autox.test.StepExecutor;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class InteractiveStepHandler implements IAsyncServerDataHandler, IEntryPoint
{
	private static Logger logger = LogManager.getLogger(InteractiveStepHandler.class);

	private static String stepHolderTemplate;
	
	static
	{
		try
		{
			stepHolderTemplate = IOUtils.toString(InteractiveStepHandler.class.getResourceAsStream("/step-holder-template.xml"), Charset.defaultCharset());
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
	public boolean processData(Serializable data)
	{
		if(!(data instanceof InteractiveExecuteSteps))
		{
			return false;
		}
		
		if(!automationContext.isReadyToInteract())
		{
			logger.warn("As the server is not yet ready to interact, interactive steps send to server are ignored.");
			return false;
		}
		
		InteractiveExecuteSteps steps = (InteractiveExecuteSteps) data;
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
		InteractiveEnvironmentContext interactiveContext = automationContext.getInteractiveEnvironmentContext();
		
		if(interactiveContext.getLastTestSuite() != null)
		{
			automationContext.setActiveTestSuite(interactiveContext.getLastTestSuite());
		}
		else
		{
			automationContext.setActiveTestSuite(testSuite);
		}
		
		if(interactiveContext.getLastTestCase() != null)
		{
			automationContext.setActiveTestCase(interactiveContext.getLastTestCase(), null);
		}
		else
		{
			automationContext.setActiveTestCase(testCase, null);
		}
		
		automationContext.getExecutionStack().push(this);
		
		try
		{
			for(IStep step : steps)
			{
				try
				{
					StepExecutor.executeStep(automationContext, executionLogger, step);
				} catch(Exception ex)
				{
					logger.error("An error occurred while executing interactive step: " + step, ex);
					
					StepExecutor.handleException(automationContext, testCase, step, executionLogger, ex, null, null);
					break;
				}
			}
		}finally
		{
			automationContext.getExecutionStack().pop(this);
		}
	}
	
	@Override
	public String toText()
	{
		return "[Interactive]";
	}
}
