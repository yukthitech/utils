package com.yukthitech.automation.ui.steps;

import org.openqa.selenium.WebElement;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.ui.common.AutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Waits for locator to be part of the page and is visible.
 * @author akiran
 */
@Executable("waitFor")
public class WaitForStep implements IStep
{
	/**
	 * locator to wait for.
	 */
	private String locator;
	
	/**
	 * If true, this step waits for element with specified locator gets removed or hidden.
	 */
	private boolean hidden = false;

	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Waiting for element: {}", locator);
		
		AutomationUtils.validateWithWait(() -> {
			WebElement element = AutomationUtils.findElement(context, null, locator);
			
			if(hidden)
			{
				return (element == null || !element.isDisplayed());
			}
			
			return (element != null && element.isDisplayed());
		}, AutomationUtils.FIVE_SECONDS, "Waiting for element: " + locator, 
			new InvalidStateException("Failed to find element - " + locator));
	}

	/**
	 * Gets the locator to wait for.
	 *
	 * @return the locator to wait for
	 */
	public String getLocator()
	{
		return locator;
	}

	/**
	 * Sets the locator to wait for.
	 *
	 * @param locator the new locator to wait for
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Locator: ").append(locator);
		builder.append(",").append("Hidden: ").append(hidden);

		builder.append("]");
		return builder.toString();
	}
}
