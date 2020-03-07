package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Simulates the Right clicks the specified target.
 * 
 * @author akiran
 */
@Executable(name = "uiRightClick", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Right clicks the specified target")
public class RightClickStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * locator for button.
	 */
	@Param(description = "Locator of the element to be triggered. Out of located elements, first element will be clicked.", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace("Right-Clicking the element specified by locator: {}", locator);

		try
		{
			UiAutomationUtils.validateWithWait(() -> 
			{
				WebElement webElement = UiAutomationUtils.findElement(context, super.parentElement, locator);

				if(webElement == null)
				{
					exeLogger.error("Failed to find element with locator: {}", getLocatorWithParent(locator));
					throw new NullPointerException("Failed to find element with locator: " + getLocatorWithParent(locator));
				}

				try
				{
					SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
					WebDriver driver = seleniumConfiguration.getWebDriver();

					Actions actions = new Actions(driver);
					actions.contextClick(webElement).build().perform();;
					return true;
				} catch(RuntimeException ex)
				{
					if(ex.getMessage().toLowerCase().contains("not clickable"))
					{
						return false;
					}
	
					throw ex;
				}
			} , IAutomationConstants.FIVE_SECONDS, IAutomationConstants.ONE_SECOND, 
					"Waiting for element to be clickable: " + getLocatorWithParent(locator), 
					new InvalidStateException("Failed to right click element - " + getLocatorWithParent(locator)));
		}catch(InvalidStateException ex)
		{
			exeLogger.error(ex, "Failed to right click element - {}", getLocatorWithParent(locator));
			throw new TestCaseFailedException(this, "Failed to right click element - {}", getLocatorWithParent(locator), ex);
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
