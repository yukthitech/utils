package com.yukthitech.autox;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.monitor.MonitorLogMessage;
import com.yukthitech.autox.test.log.ExecutionLogData;
import com.yukthitech.autox.test.log.LogLevel;
import com.yukthitech.utils.MessageFormatter;

/**
 * A simple internal logger to consolidate execution messages in test result.
 * 
 * @author akiran
 */
public class ExecutionLogger
{
	private static Logger logger = LogManager.getLogger(ExecutionLogger.class);

	private static int fileIndex = 1;
	
	/**
	 * Context as part of which this logger is created.
	 */
	private AutomationContext automationContext;
	
	/**
	 * Log data information.
	 */
	private ExecutionLogData executionLogData;
	
	/**
	 * Flag indicating if logging is disabled or not.
	 */
	private boolean disabled = false;
	
	/**
	 * Mode to be prepended for every log message.
	 */
	private String mode;

	public ExecutionLogger(AutomationContext automationContext, String executorName, String executorDescription)
	{
		this.automationContext = automationContext;
		this.executionLogData = new ExecutionLogData(executorName, executorDescription);
	}
	
	/**
	 * Sets the mode to be prepended for every log message.
	 *
	 * @param mode the new mode to be prepended for every log message
	 */
	public void setMode(String mode)
	{
		this.mode = mode;
	}
	
	/**
	 * Clears the mode from the logger.
	 */
	public void clearMode()
	{
		this.mode = null;
	}
	
	/**
	 * Sets the flag indicating if logging is disabled or not.
	 *
	 * @param disabled the new flag indicating if logging is disabled or not
	 */
	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}
	
	/**
	 * Gets the flag indicating if logging is disabled or not.
	 *
	 * @return the flag indicating if logging is disabled or not
	 */
	public boolean isDisabled()
	{
		return disabled;
	}
	
	private String getSource(StackTraceElement stackTrace[])
	{
		return stackTrace[2].getFileName() + ":" + stackTrace[2].getLineNumber();
	}
	
	/**
	 * Fetches the location of the element.
	 * @param locationBased
	 * @return
	 */
	private String getSourceLocation(ILocationBased locationBased)
	{
		if(locationBased == null)
		{
			return null;
		}
		
		return locationBased.getLocation();
	}
	
	private String buildMessage(String mssgTemplate, Object... args)
	{
		String finalMssg = MessageFormatter.format(mssgTemplate, args);
		
		if(mode != null)
		{
			finalMssg = "<b>[" + mode + "]</b> " + finalMssg;
		}
		
		return finalMssg;
	}
	
	private void addMessage(ExecutionLogData.Message mssg)
	{
		executionLogData.addMessage(mssg);
		
		automationContext.sendAsyncMonitorMessage(new MonitorLogMessage(
				automationContext.getActiveTestSuite() != null ? automationContext.getActiveTestSuite().getName() : null, 
				automationContext.getActiveTestCase() != null ? automationContext.getActiveTestCase().getName() : null, 
				automationContext.getActiveTestCaseData() != null ? automationContext.getActiveTestCaseData().getName() : null, 
				mssg, 
				automationContext.isSetupExecution(),
				automationContext.isCleanupExecution())
		);
	}

	/**
	 * Used to log error messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void error(ILocationBased source, String mssgTemplate, Object... args)
	{
		String finalMssg = buildMessage(mssgTemplate, args);
		logger.error(finalMssg);
		
		addMessage(new ExecutionLogData.Message( getSourceLocation(source), getSource(Thread.currentThread().getStackTrace()), LogLevel.ERROR, finalMssg, new Date()));
	}

	/**
	 * Used to log error messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param th Throwable stack trace.
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void error(ILocationBased source, Throwable th, String mssgTemplate, Object... args)
	{
		String finalMssg = buildMessage(mssgTemplate, args);
		logger.error(finalMssg, th);
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		
		printWriter.println(finalMssg);
		th.printStackTrace(printWriter);
		printWriter.flush();
		
		addMessage(new ExecutionLogData.Message( getSourceLocation(source), getSource(Thread.currentThread().getStackTrace()), LogLevel.ERROR, stringWriter.toString(), new Date()));
	}

	/**
	 * Used to log debug messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void debug(ILocationBased source, String mssgTemplate, Object... args)
	{
		if(disabled)
		{
			return;
		}
		
		String finalMssg = buildMessage(mssgTemplate, args);

		logger.debug(finalMssg);
		addMessage(new ExecutionLogData.Message( getSourceLocation(source), getSource(Thread.currentThread().getStackTrace()), LogLevel.DEBUG, finalMssg, new Date()));
	}
	
	/**
	 * Used to log trace messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void trace(ILocationBased source, String mssgTemplate, Object... args)
	{
		if(disabled)
		{
			return;
		}
		
		String finalMssg = buildMessage(mssgTemplate, args);

		logger.trace(finalMssg);
		addMessage(new ExecutionLogData.Message( getSourceLocation(source), getSource(Thread.currentThread().getStackTrace()), LogLevel.TRACE, finalMssg, new Date()));
	}

	/**
	 * Logs the message at specified level.
	 * @param source location from where logging is being done
	 * @param logLevel level of log
	 * @param mssgTemplate msg template
	 * @param args arguments for message
	 */
	public void log(ILocationBased source, LogLevel logLevel, String mssgTemplate, Object... args)
	{
		if(disabled)
		{
			return;
		}
		
		String finalMssg = buildMessage(mssgTemplate, args);

		logger.debug(finalMssg);
		addMessage(new ExecutionLogData.Message( getSourceLocation(source), getSource(Thread.currentThread().getStackTrace()), logLevel, finalMssg, new Date()));
	}

	/**
	 * Adds the specified image file to the debug log.
	 * @param source location from where logging is being done
	 * @param name Name of the image file for easy identification
	 * @param message Message to be logged along with image
	 * @param imageFile Image to be logged
	 * @param logLevel level to be used.
	 */
	public void logImage(ILocationBased source, String name, String message, File imageFile, LogLevel logLevel)
	{
		if(disabled)
		{
			return;
		}
		
		if(logLevel == null)
		{
			logLevel = LogLevel.DEBUG;
		}
		
		if(mode != null)
		{
			message = (message != null) ? "" : message;
			message = "<b>[" + mode + "]</b> " + message;
		}
		
		int dotIdx = imageFile.getName().lastIndexOf(".");
		String extension = "";
		
		if(dotIdx > 0)
		{
			extension = imageFile.getName().substring(dotIdx);
		}
		
		if(name != null)
		{
			name = name + "_" + System.currentTimeMillis() + "_" + (fileIndex++) + extension;
		}
		else
		{
			name = System.currentTimeMillis() + "_" + (fileIndex++) + extension;
		}
		
		addMessage(new ExecutionLogData.ImageMessage( getSourceLocation(source), getSource(Thread.currentThread().getStackTrace()), logLevel, message, new Date(), name, imageFile));
	}
	
	/**
	 * Gets the log data information.
	 *
	 * @return the log data information
	 */
	public ExecutionLogData getExecutionLogData()
	{
		return executionLogData;
	}
}
