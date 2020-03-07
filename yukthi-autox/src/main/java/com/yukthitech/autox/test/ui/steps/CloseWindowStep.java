package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Helps in switching between windows.
 * 
 * @author akiran
 */
@Executable(name = "uiCloseWindow", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Closes the current window.")
public class CloseWindowStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace("Closing current window");

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
		
		driver.close();
		return true;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[]");
		return builder.toString();
	}
}
