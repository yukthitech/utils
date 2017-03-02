package com.yukthitech.automation.test.common.steps;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;

/**
 * Loads specified properties file as map on to context.
 * @author akiran
 */
@Executable(name = "log", message = "Logs specified message")
public class LogStep implements IStep 
{
	/**
	 * Logs specified message in ui.
	 */
	private String message;
	
	/**
	 * Sets the logs specified message in ui.
	 *
	 * @param message the new logs specified message in ui
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		exeLogger.debug(message);
	}
}
