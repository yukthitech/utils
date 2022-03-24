package com.yukthitech.autox.test.ui.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Quits the driver. In order to user driver again it has to be initialized.
 * @author akiran
 */
@Executable(name = "uiQuitSession", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Quits the driver. In order to user driver again it has to be initialized.")
public class QuitSessionStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Closing current session");
		
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		
		seleniumConfiguration.getWebDriver().quit();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Quit Session");
		return builder.toString();
	}

}
