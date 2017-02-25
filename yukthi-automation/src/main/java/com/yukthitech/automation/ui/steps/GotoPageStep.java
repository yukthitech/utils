package com.yukthitech.automation.ui.steps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;

/**
 * Goes to the specified page url.
 * @author akiran
 */
@Executable("gotoPage")
public class GotoPageStep implements IStep
{
	private static Logger logger = LogManager.getLogger(GotoPageStep.class);
	
	/**
	 * Url to which browser should be taken.
	 */
	private String url;

	/**
	 * Takes the browser to specified page url.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		logger.trace("Going to page: {}", url);
		
		exeLogger.debug("Going to page url - {}", url);
		
		WebDriver driver = context.getWebDriver();
		driver.navigate().to(url);
		
		//maximize the browser window size
		driver.manage().window().maximize();
	}

	/**
	 * Gets the url to which browser should be taken.
	 *
	 * @return the url to which browser should be taken
	 */
	public String getUrl()
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

		builder.append("Page: ").append(url);

		builder.append("]");
		return builder.toString();
	}
}
