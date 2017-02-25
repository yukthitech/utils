package com.yukthitech.automation.ui.steps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;

/**
 * Simulates the click event on the specified button.
 * @author akiran
 */
@Executable("closeSession")
public class CloseSessionStep implements IStep
{
	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(CloseSessionStep.class);

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		logger.trace("Closing current session");
		
		context.getWebDriver().close();
		context.resetDriver();
	}
}
