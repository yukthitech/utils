package com.yukthitech.automation.test.ui.steps;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.config.SeleniumPlugin;

/**
 * Simulates the click event on the specified button.
 * @author akiran
 */
@Executable(name = "closeSession", requiredPluginTypes = SeleniumPlugin.class, message = "Closes the curren browser window.")
public class CloseSessionStep implements IStep
{
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Closing current session");
		
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		
		seleniumConfiguration.getWebDriver().close();
		seleniumConfiguration.resetDriver();
	}
}
