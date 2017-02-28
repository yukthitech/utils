package com.yukthitech.automation.test.ui.steps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.config.SeleniumConfiguration;

/**
 * Goes to the specified page url.
 * @author akiran
 */
@Executable(value = "gotoPage", requiredConfigurationTypes = SeleniumConfiguration.class, message = "Goes to specified page")
public class GotoPageStep implements IStep
{
	private static Logger logger = LogManager.getLogger(GotoPageStep.class);
	
	/**
	 * Url to which browser should be taken.
	 */
	private String uri;

	/**
	 * Takes the browser to specified page url.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		logger.trace("Going to page: {}", uri);
		exeLogger.debug("Going to page url - {}", uri);

		SeleniumConfiguration seleniumConfiguration = context.getConfiguration(SeleniumConfiguration.class);
		
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
