package com.yukthitech.automation.test.ui.steps;

import org.openqa.selenium.WebDriver;

import com.yukthitech.automation.AbstractStep;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.config.SeleniumPlugin;

/**
 * Goes to the specified page url.
 * @author akiran
 */
@Executable(name = "gotoPage", requiredPluginTypes = SeleniumPlugin.class, message = "Loads page with specified uri")
public class GotoPageStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Url to which browser should be taken.
	 */
	@Param(description = "URI of the page to load")
	private String uri;

	/**
	 * Takes the browser to specified page url.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Going to page with uri - {}", uri);

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		
		WebDriver driver = seleniumConfiguration.getWebDriver();
		driver.navigate().to( seleniumConfiguration.getResourceUrl(uri) );
		
		//maximize the browser window size
		driver.manage().window().maximize();
	}

	/**
	 * Gets the url to which browser should be taken.
	 *
	 * @return the url to which browser should be taken
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * Sets the url to which browser should be taken.
	 *
	 * @param url the new url to which browser should be taken
	 */
	public void setUri(String url)
	{
		this.uri = url;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Uri: ").append(uri);

		builder.append("]");
		return builder.toString();
	}
}
