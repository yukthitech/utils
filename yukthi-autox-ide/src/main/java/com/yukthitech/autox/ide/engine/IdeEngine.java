package com.yukthitech.autox.ide.engine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.TestSuiteParserHandler;
import com.yukthitech.autox.ide.model.ExecutedStep;
import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.test.StepExecutor;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.event.EventListenerManager;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Ide engine which connects all the components.
 * @author akiran
 */
public class IdeEngine
{
	private static Logger logger = LogManager.getLogger(IdeEngine.class);
	
	/**
	 * Manager to manage listeners.
	 */
	private EventListenerManager<IdeEngineListener> listenerManager = EventListenerManager.newEventListenerManager(IdeEngineListener.class, false);
	
	private static String stepHolderTemplate;
	
	static
	{
		try
		{
			stepHolderTemplate = IOUtils.toString(IdeEngine.class.getResourceAsStream("/step-holder-template.xml"));
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading resource: /step-holder-template.xml", ex);
		}
	}
	
	
	/**
	 * State of the ide.
	 */
	private IdeState state;
	
	/**
	 * Automation context used for step execution.
	 */
	private AutomationContext context;
	
	/**
	 * parser handler to parse steps.
	 */
	private TestSuiteParserHandler testSuiteParserHandler;
	
	private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	/**
	 * Initializes the ide engine.
	 * @param ideState
	 */
	public void init(IdeState ideState)
	{
		try
		{
			logger.debug("Initializing ide engine..");
			this.state = ideState;

			if(CollectionUtils.isNotEmpty(ideState.getSteps()))
			{
				reexecute();
			}
			else
			{
				context = AutomationLauncher.loadAutomationContext( new File(state.getApplicationConfigFile()), state.getCommandLineArguments());
				testSuiteParserHandler = new TestSuiteParserHandler(context);
			}
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while initializing the context", ex);
		}
	}
	
	public void removeStep(ExecutedStep step)
	{
		if(this.state.removeStep(step))
		{
			listenerManager.get().stepRemoved(step);
		}
	}
	
	public List<ExecutedStep> getSteps()
	{
		return state.getSteps();
	}
	
	public IdeState getState()
	{
		return state;
	}
	
	/**
	 * Gets the automation context used for step execution.
	 *
	 * @return the automation context used for step execution
	 */
	public AutomationContext getContext()
	{
		return context;
	}
	
	/**
	 * Executes the specified steps.
	 * @param steps
	 * @return
	 */
	private String executeSteps(List<IStep> steps)
	{
		ExecutionLogger exeLogger = new ExecutionLogger("Ide", "Ide");
		
		TestCase dummy = new TestCase();
		dummy.setName("dummy");
		
		for(IStep step : steps)
		{
			try
			{
				StepExecutor.executeStep(context, exeLogger, step);
			} catch(Exception ex)
			{
				StepExecutor.handleException(context, dummy, step, exeLogger, ex, null);
				break;
			}
		}

		try
		{
			return freeMarkerEngine.processTemplate(
					"output-log.html", 
					IOUtils.toString(IdeEngine.class.getResourceAsStream("/output-log.html")), 
					CommonUtils.toMap("messages", exeLogger.getExecutionLogData().getMessages())
				);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while generating output log html", ex);
		}
	}
	
	private void sendOutput(String output, boolean error)
	{
		StringBuilder finalHtml = new StringBuilder( "<html><body>" );
		
		if(error)
		{
			finalHtml.append("<div style=\"color: red\">");
		}
		else
		{
			finalHtml.append("<div>");
		}
		
		finalHtml.append(output);
		
		finalHtml.append("</div></body></html>");
		
		listenerManager.get().sendOutput(finalHtml.toString());
	}
	
	private boolean executeOnly(StepDetails step)
	{
		//invoke the listener
		listenerManager.get().executingStep(step);
		
		//extract steps from input text
		StepHolder stepHolder = new StepHolder();
		String stepXml = CommonUtils.replaceExpressions(
				CommonUtils.toMap("steps", step.getText()), 
				stepHolderTemplate, null);
		
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(stepXml.getBytes());
			
			XMLBeanParser.parse(bis, stepHolder, testSuiteParserHandler);
		} catch(Exception ex)
		{
			logger.error("An error occurred while parsing input step xml.", ex);
			sendOutput("An error occurred while parsing input step xml.<br/>Error: " + ex, true);
			return false;
		}
		
		//execute the extracted steps
		try
		{
			String output = executeSteps(stepHolder.getSteps());
			sendOutput(output, false);
			
			return true;
		} catch(Exception ex)
		{
			logger.error("An error occurred while executing specified steps.", ex);
			sendOutput("An error occurred while executing specified steps.<br/>Error: " + ex, true);
			return false;
		}
	}
	
	public boolean executeStep(StepDetails step)
	{
		if(!executeOnly(step))
		{
			return false;
		}

		//execute the extracted steps
		try
		{
			ExecutedStep executedStep = state.addStep(step.getText(), step.getRtfText());
			listenerManager.get().stepExecuted(executedStep);
			
			return true;
		} catch(Exception ex)
		{
			logger.error("An error occurred while executing specified steps.", ex);
			sendOutput("An error occurred while executing specified steps.<br/>Error: " + ex, true);
			return false;
		}
	}
	
	/**
	 * Adds the specified listener to this engine.
	 * @param listener
	 */
	public void addIdeEngineListener(IdeEngineListener listener)
	{
		this.listenerManager.addListener(listener);
	}

	
	public void exportActionXml(File file)
	{
		List<ExecutedStep> steps = state.getSteps();
		
		if(steps == null || steps.isEmpty())
		{
			throw new InvalidStateException("No steps found to export");
		}
		
		StringBuilder builder = new StringBuilder();
		
		for(ExecutedStep step : steps)
		{
			builder.append(step.getText()).append("\n");
		}
		
		String stepXml = CommonUtils.replaceExpressions(
				CommonUtils.toMap("steps", builder.toString()), 
				stepHolderTemplate, null);
		
		try
		{
			FileUtils.write(file, stepXml);
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while exporting steps to specified file: " + file.getPath(), ex);
		}
	}
	
	/**
	 * Destroys current context and re-executes all the steps present in current state.
	 */
	public void reexecute()
	{
		if(context != null)
		{
			context.close();
		}

		try
		{
			logger.debug("Re-initializing ide engine..");
			context = AutomationLauncher.loadAutomationContext( new File(state.getApplicationConfigFile()), state.getCommandLineArguments());
			testSuiteParserHandler = new TestSuiteParserHandler(context);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while re-initializing the context", ex);
		}
		
		if(CollectionUtils.isEmpty(state.getSteps()))
		{
			listenerManager.get().stateLoaded();
			return;
		}
		
		for(ExecutedStep step : state.getSteps())
		{
			executeOnly(new StepDetails(step.getText(), step.getRtfText()));
		}
		
		listenerManager.get().stateLoaded();
	}
}
