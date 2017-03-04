package com.yukthitech.automation.test.ui.steps;

import org.openqa.selenium.WebElement;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.config.SeleniumPlugin;
import com.yukthitech.automation.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Simulates the click event on the specified button.
 * 
 * @author akiran
 */
@Executable(name = "click", requiredPluginTypes = SeleniumPlugin.class, message = "Clicks the specified target")
public class ClickStep implements IStep
{
	/**
	 * locator for button.
	 */
	@Param(description = "Locator of the element to be triggered.")
	private String locator;
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Clicking the element specified by locator: {}", locator);

		WebElement webElement = UiAutomationUtils.findElement(context, null, locator);

		if(webElement == null)
		{
			exeLogger.error("Failed to find element with locator: {}", locator);
			throw new NullPointerException("Failed to find element with locator: " + locator);
		}

		try
		{
			UiAutomationUtils.validateWithWait(() -> 
			{
				try
				{
					webElement.click();
					return true;
				} catch(RuntimeException ex)
				{
					if(ex.getMessage().toLowerCase().contains("not clickable"))
					{
						return false;
					}
	
					throw ex;
				}
			} , UiAutomationUtils.FIVE_SECONDS, "Waiting for element to be clickable: " + locator, new InvalidStateException("Failed to click element - " + locator));
		}catch(InvalidStateException ex)
		{
			exeLogger.error(ex, "Failed to click element - {}", locator);
			throw ex;
		}
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
