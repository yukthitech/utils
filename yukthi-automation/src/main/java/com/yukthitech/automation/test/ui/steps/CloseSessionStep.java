package com.yukthitech.automation.test.ui.steps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.config.SeleniumConfiguration;

/**
 * Simulates the click event on the specified button.
 * @author akiran
 */
@Executable(name = "closeSession", requiredConfigurationTypes = SeleniumConfiguration.class, message = "Closes the browser")
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
		
		SeleniumConfiguration seleniumConfiguration = context.getConfiguration(SeleniumConfiguration.class);
		
		seleniumConfiguration.getWebDriver().close();
		seleniumConfiguration.resetDriver();
	}
}
