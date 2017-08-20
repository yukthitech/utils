package com.yukthitech.autox;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	 * Log data information.
	 */
	private ExecutionLogData executionLogData;
	
	/**
	 * Flag indicating if logging is disabled or not.
	 */
	private boolean disabled = false;

	public ExecutionLogger(String executorName, String executorDescription)
	{
		this.executionLogData = new ExecutionLogData(executorName, executorDescription);
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
	 * Used to log error messages as part of current execution.
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void error(String mssgTemplate, Object... args)
	{
		String finalMssg = MessageFormatter.format(mssgTemplate, args);
		logger.error(finalMssg);
		
		executionLogData.addMessage(new ExecutionLogData.Message( getSource(Thread.currentThread().getStackTrace()), LogLevel.ERROR, finalMssg, new Date()));
	}

	/**
	 * Used to log error messages as part of current execution.
	 * @param th Throwable stack trace.
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void error(Throwable th, String mssgTemplate, Object... args)
	{
		String finalMssg = MessageFormatter.format(mssgTemplate, args);
		logger.error(finalMssg, th);
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		
		printWriter.println(finalMssg);
		th.printStackTrace(printWriter);
		printWriter.flush();
		
		executionLogData.addMessage(new ExecutionLogData.Message( getSource(Thread.currentThread().getStackTrace()), LogLevel.ERROR, stringWriter.toString(), new Date()));
	}

	/**
	 * Used to log debug messages as part of current execution.
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void debug(String mssgTemplate, Object... args)
	{
		if(disabled)
		{
			return;
		}
		
		String finalMssg = MessageFormatter.format(mssgTemplate, args);

		logger.debug(finalMssg);
		executionLogData.addMessage(new ExecutionLogData.Message( getSource(Thread.currentThread().getStackTrace()), LogLevel.DEBUG, finalMssg, new Date()));
	}
	
	/**
	 * Used to log trace messages as part of current execution.
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void trace(String mssgTemplate, Object... args)
	{
		if(disabled)
		{
			return;
		}
		
		String finalMssg = MessageFormatter.format(mssgTemplate, args);

		logger.trace(finalMssg);
		executionLogData.addMessage(new ExecutionLogData.Message( getSource(Thread.currentThread().getStackTrace()), LogLevel.TRACE, finalMssg, new Date()));
	}

	/**
	 * Adds the specified image file to the debug log.
	 * @param name Name of the image file for easy identification
	 * @param message Message to be logged along with image
	 * @param imageFile Image to be logged
	 * @param logLevel level to be used.
	 */
	public void logImage(String name, String message, File imageFile, LogLevel logLevel)
	{
		if(disabled)
		{
			return;
		}
		
		if(logLevel == null)
		{
			logLevel = LogLevel.DEBUG;
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
		
		executionLogData.addMessage(new ExecutionLogData.ImageMessage( getSource(Thread.currentThread().getStackTrace()), logLevel, message, new Date(), name, imageFile));
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
