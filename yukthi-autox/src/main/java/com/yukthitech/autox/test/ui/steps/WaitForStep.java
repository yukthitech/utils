package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Waits for locator to be part of the page and is visible.
 * @author akiran
 */
@Executable(name = "waitFor", requiredPluginTypes = SeleniumPlugin.class, message = "Waits for specified element to become visible/hidden")
public class WaitForStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * locator to wait for.
	 */
	@Param(description = "Locator of the element to be waited for", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	/**
	 * If true, this step waits for element with specified locator gets removed or hidden.
	 */
	@Param(description = "If true, this step waits for element with specified locator gets removed or hidden.\nDefault: false", required = false)
	private String hidden = "false";

	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Waiting for element '{}' to become {}", locator, "true".equals(hidden) ? "Invisible" : "Visible");
		
		try
		{
			UiAutomationUtils.validateWithWait(() -> 
			{
				WebElement element = UiAutomationUtils.findElement(context, null, locator);
				
				if("true".equals(hidden))
				{
					return (element == null || !element.isDisplayed());
				}
				
				return (element != null && element.isDisplayed());
			}, UiAutomationUtils.FIVE_SECONDS, "Waiting for element: " + locator, 
				new InvalidStateException("Failed to find element - " + locator));
			
		} catch(InvalidStateException ex)
		{
			exeLogger.error(ex, ex.getMessage());
			throw new TestCaseFailedException(ex.getMessage(), ex);
		}
		
		return true;
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
	
	/**
	 * Sets the if true, this step waits for element with specified locator gets removed or hidden.
	 *
	 * @param hidden the new if true, this step waits for element with specified locator gets removed or hidden
	 */
	public void setHidden(String hidden)
	{
		this.hidden = hidden;
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
