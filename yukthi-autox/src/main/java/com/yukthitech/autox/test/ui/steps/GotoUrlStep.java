package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Goes to the specified page url.
 * @author akiran
 */
@Executable(name = "uiGotoUrl", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Loads page with specified url")
public class GotoUrlStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Url to which browser should be taken.
	 */
	@Param(description = "URL of the page to load")
	private String url;

	/**
	 * Takes the browser to specified page url.
	 * @param context Current automation context 
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Going to page with url - {}", url);

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		
		WebDriver driver = seleniumConfiguration.getWebDriver();
		driver.navigate().to(url);
		
		return true;
	}

	/**
	 * Gets the url to which browser should be taken.
	 *
	 * @return the url to which browser should be taken
	 */
	public String getUri()
	{
		return url;
	}

	/**
	 * Sets the url to which browser should be taken.
	 *
	 * @param url the new url to which browser should be taken
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Url: ").append(url);

		builder.append("]");
		return builder.toString();
	}
}
