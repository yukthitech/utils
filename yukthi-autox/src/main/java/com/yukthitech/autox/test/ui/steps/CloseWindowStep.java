package com.yukthitech.autox.test.ui.steps;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Helps in switching between windows.
 * 
 * @author akiran
 */
@Executable(name = "uiCloseWindow", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Closes the specified/current window.")
public class CloseWindowStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the window. If none is specified, current window will be closed.
	 */
	@Param(description = "Name of the window. If none is specified, current window will be closed.", required = false)
	private String name;
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Closing window with name: {}", name);
		
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();

		if(StringUtils.isEmpty(name))
		{
			exeLogger.trace("As no name is specified closing current window");
			driver.close();
			return;
		}
		
		String openScript = String.format("var wind = window.open('', '%s'); wind.close();", name);
		((JavascriptExecutor) driver).executeScript(openScript);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Close Window");
		return builder.toString();
	}
}
