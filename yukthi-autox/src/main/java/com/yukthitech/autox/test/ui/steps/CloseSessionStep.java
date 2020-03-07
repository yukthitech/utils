package com.yukthitech.autox.test.ui.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Simulates the click event on the specified button.
 * @author akiran
 */
@Executable(name = "uiCloseSession", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Closes the current browser window.")
public class CloseSessionStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Closing current session");
		
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		
		seleniumConfiguration.getWebDriver().quit();
		//seleniumConfiguration.getWebDriver().close();
		seleniumConfiguration.resetDriver();
		
		return true;
	}
}
