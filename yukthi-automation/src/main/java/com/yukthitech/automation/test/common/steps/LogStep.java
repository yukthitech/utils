package com.yukthitech.automation.test.common.steps;

import com.yukthitech.automation.AbstractStep;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.Param;

/**
 * Logs the specified message using execution logger.
 * @author akiran
 */
@Executable(name = "log", message = "Logs specified message")
public class LogStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Logs specified message in ui.
	 */
	@Param(description = "Message to log")
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
	public void execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		exeLogger.debug(message);
	}
}
