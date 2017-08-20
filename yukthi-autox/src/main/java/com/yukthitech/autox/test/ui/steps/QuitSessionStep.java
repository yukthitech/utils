package com.yukthitech.autox.test.ui.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Quits the driver. In order to user driver again it has to be initialized.
 * @author akiran
 */
@Executable(name = "quitSession", requiredPluginTypes = SeleniumPlugin.class, message = "Quits the driver. In order to user driver again it has to be initialized.")
public class QuitSessionStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Closing current session");
		
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		
		seleniumConfiguration.getWebDriver().quit();
		
		return true;
	}
}
