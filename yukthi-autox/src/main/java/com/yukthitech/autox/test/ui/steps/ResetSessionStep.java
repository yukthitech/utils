package com.yukthitech.autox.test.ui.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Resets the driver for usage.
 * @author akiran
 */
@Executable(name = {"uiResetSession", "resetSession"}, requiredPluginTypes = SeleniumPlugin.class, message = "Resets the driver for usage.")
public class ResetSessionStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Closing current session");
		
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		
		seleniumConfiguration.resetDriver();
		
		return true;
	}
}
