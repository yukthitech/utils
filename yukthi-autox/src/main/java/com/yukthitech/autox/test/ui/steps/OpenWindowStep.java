package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Opens new window with specifie name and url.
 * 
 * @author akiran
 */
@Executable(name = "uiOpenWindow", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Opens new window with specifie name and url.")
public class OpenWindowStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Url to be opened.
	 */
	@Param(description = "Url to be opened.")
	private String url;
	
	/**
	 * Name of the window being opened.
	 */
	@Param(description = "Name of the window being opened.")
	private String name;
	
	/**
	 * Sets the url to be opened.
	 *
	 * @param url the new url to be opened
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * Sets the name of the window being opened.
	 *
	 * @param name the new name of the window being opened
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace("Opening window '{}' with url: {}", name, url);

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
		
		String openScript = String.format("window.open('%s', '%s')", url, name);
		
		((JavascriptExecutor) driver).executeScript(openScript);
		
		driver.switchTo().window(name);
		
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
		StringBuilder builder = new StringBuilder();
		builder.append("Open Window [");

		builder.append("Name: ").append(name);
		builder.append(", Url: ").append(url);

		builder.append("]");
		return builder.toString();
	}
}
