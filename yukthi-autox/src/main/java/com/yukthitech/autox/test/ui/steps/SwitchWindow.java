package com.yukthitech.autox.test.ui.steps;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Helps in switching between windows.
 * 
 * @author akiran
 */
@Executable(name = "switchWindow", requiredPluginTypes = SeleniumPlugin.class, message = "Helps in switching between windows")
public class SwitchWindow extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Locator of the window. If none is specified, main window will be selected..
	 */
	@Param(description = "Locator of the window. If none is specified, main window will be selected.", required = false)
	private String locator;
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace(this, "Switching to window: {}", locator);

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
		
		if(StringUtils.isBlank(locator))
		{
			exeLogger.trace(this, "As no locator is specified switching to main window");
			locator = driver.getWindowHandle();
		}

		driver.switchTo().window(locator);
		
		return true;
	}

	/**
	 * Sets the locator for button.
	 *
	 * @param locator
	 *            the new locator for button
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
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
		builder.append("[");

		builder.append("Locator: ").append(locator);

		builder.append("]");
		return builder.toString();
	}
}