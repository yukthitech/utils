package com.yukthitech.automation.test.log;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.utils.MessageFormatter;

/**
 * A simple internal logger to consolidate execution messages in test result.
 * 
 * @author akiran
 */
public class TestExecutionLogger implements IExecutionLogger
{
	private static Logger logger = LogManager.getLogger(TestExecutionLogger.class);

	private static int fileIndex = 1;
	
	/**
	 * Log data information.
	 */
	private ExecutionLogData executionLogData;

	public TestExecutionLogger(String executorName, String executorDescription)
	{
		this.executionLogData = new ExecutionLogData(executorName, executorDescription);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ui.automation.IExecutionLogger#error(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public void error(String mssgTemplate, Object... args)
	{
		String finalMssg = MessageFormatter.format(mssgTemplate, args);
		logger.error(finalMssg);
		
		executionLogData.addMessage(new ExecutionLogData.Message(LogLevel.ERROR, finalMssg, new Date()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ui.automation.IExecutionLogger#error(java.lang.Throwable,
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public void error(Throwable th, String mssgTemplate, Object... args)
	{
		String finalMssg = MessageFormatter.format(mssgTemplate, args);
		logger.error(finalMssg, th);
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		
		printWriter.println(finalMssg);
		th.printStackTrace(printWriter);
		printWriter.flush();
		
		executionLogData.addMessage(new ExecutionLogData.Message(LogLevel.ERROR, stringWriter.toString(), new Date()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ui.automation.IExecutionLogger#debug(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public void debug(String mssgTemplate, Object... args)
	{
		String finalMssg = MessageFormatter.format(mssgTemplate, args);

		logger.debug(finalMssg);
		executionLogData.addMessage(new ExecutionLogData.Message(LogLevel.DEBUG, finalMssg, new Date()));
	}
	
	@Override
	public void trace(String mssgTemplate, Object... args)
	{
		String finalMssg = MessageFormatter.format(mssgTemplate, args);

		logger.trace(finalMssg);
		executionLogData.addMessage(new ExecutionLogData.Message(LogLevel.TRACE, finalMssg, new Date()));
	}

	@Override
	public void logImage(String name, String message, File imageFile)
	{
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
		
		executionLogData.addMessage(new ExecutionLogData.ImageMessage(LogLevel.DEBUG, message, new Date(), name, imageFile));
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
