package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Simulates the click event on the specified button.
 * 
 * @author akiran
 */
@Executable(name = "uiClick", requiredPluginTypes = SeleniumPlugin.class, message = "Clicks the specified target")
public class ClickStep extends AbstractPostCheckStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * locator for button.
	 */
	@Param(description = "Locator of the element to be clicked. Out of located elements, first element will be clicked.", sourceType = SourceType.UI_LOCATOR)
	private String locator;

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace("Clicking the element specified by locator: {}", locator);

		try
		{
			ObjectWrapper<Boolean> clickedAtleastOnce = new ObjectWrapper<>(false);
			
			UiAutomationUtils.validateWithWait(() -> 
			{
				try
				{
					WebElement webElement = UiAutomationUtils.findElement(context, super.parentElement, locator);

					if(webElement == null)
					{
						exeLogger.error("Failed to find element with locator: {}", getLocatorWithParent(locator));
						throw new NullPointerException("Failed to find element with locator: " + getLocatorWithParent(locator));
					}

					if(clickedAtleastOnce.getValue())
					{
						if(super.isPostCheckAvailable() && doPostCheck(exeLogger, "Before re-click"))
						{
							exeLogger.trace("Before re-click as post check is successful, skipping re-click");
							return true;
						}
					}
					
					exeLogger.trace("Trying to click element specified by locator: {}", locator);
					
					webElement.click();
					clickedAtleastOnce.setValue(true);
					
					//after click check the post-check and return result approp
					return doPostCheck(exeLogger, "Post Click");
				} catch(RuntimeException ex)
				{
					exeLogger.debug("IGNORED: An error occurred while clicking locator - {}. Error: {}", locator,  "" + ex);
					
					if(UiAutomationUtils.isElementNotAvailableException(ex))
					{
						return false;
					}
	
					throw ex;
				}
			} , IAutomationConstants.SIXTY_SECONDS, IAutomationConstants.ONE_SECOND,
					"Waiting for element to be clickable: " + getLocatorWithParent(locator), 
					new InvalidStateException("Failed to click element - " + getLocatorWithParent(locator)));
		}catch(InvalidStateException ex)
		{
			exeLogger.error(ex, "Failed to click element - {}", getLocatorWithParent(locator));
			throw new TestCaseFailedException(this, "Failed to click element - {}", getLocatorWithParent(locator), ex);
		}
		
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
