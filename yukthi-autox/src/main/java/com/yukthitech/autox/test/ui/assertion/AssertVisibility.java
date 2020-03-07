package com.yukthitech.autox.test.ui.assertion;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Waits for locator to be part of the page and is visible.
 * 
 * @author akiran
 */
@Executable(name = "uiAssertVisibility", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Validates specified element is visible/hidden")
public class AssertVisibility extends AbstractUiAssert
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * locator to wait for.
	 */
	@Param(description = "Locator of the element to validate", sourceType = SourceType.UI_LOCATOR)
	private String locator;

	/**
	 * If true, this step waits for element with specified locator gets removed
	 * or hidden.
	 */
	@Param(description = "Flag indicating if the validation is for visibility or invisibility.\nDefault: true", required = false)
	private String visible = "true";

	/**
	 * Message expected in the target element.
	 */
	@Param(description = "Message expected in the target element.", required = false)
	private String message = null;

	/**
	 * Execute.
	 *
	 * @param context
	 *            the context
	 * @param exeLogger
	 *            the exe logger
	 * @return true, if successful
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		if(!"true".equals(enabled))
		{
			exeLogger.debug("Current validation is disabled. Skipping validation execution.");
			return true;
		}
		
		exeLogger.trace("Checking for element Visibility is {}", locator, "true".equals(visible) ? "Visible" : "Invisible");
		
		UiAutomationUtils.validateWithWait(() -> {
			WebElement element = UiAutomationUtils.findElement(context, parentElement, locator);

			if(!"true".equals(visible))
			{
				return (element == null || !element.isDisplayed());
			}

			return (element != null && element.isDisplayed());
		} , IAutomationConstants.FIVE_SECONDS, IAutomationConstants.ONE_SECOND, 
				"Waiting for element: " + getLocatorWithParent(locator), 
				new InvalidStateException("Failed to find element - " + getLocatorWithParent(locator)));

		if(message != null)
		{
			WebElement element = UiAutomationUtils.findElement(context, parentElement, locator);
			String actualMessage = element.getText().trim();

			if(actualMessage == null || !actualMessage.contains(message))
			{
				exeLogger.error("Expected message '{}' is not matching with actual message '{}' for locator - {}", message, actualMessage, getLocatorWithParent(locator));
				return false;
			}
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
	 * @param locator
	 *            the new locator to wait for
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/**
	 * Gets the if true, this step waits for element with specified locator gets removed or hidden.
	 *
	 * @return the if true, this step waits for element with specified locator gets removed or hidden
	 */
	public String getVisible()
	{
		return visible;
	}

	/**
	 * Sets the if true, this step waits for element with specified locator gets removed or hidden.
	 *
	 * @param visible the new if true, this step waits for element with specified locator gets removed or hidden
	 */
	public void setVisible(String visible)
	{
		this.visible = visible;
	}

	/**
	 * Gets the message expected in the target element.
	 *
	 * @return the message expected in the target element
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the message expected in the target element.
	 *
	 * @param message
	 *            the new message expected in the target element
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String comma = ", ";
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("Locator: ").append(locator);
		builder.append(comma).append("Visible: ").append(visible);
		builder.append(comma).append("Message: ").append(message);

		builder.append("]");
		return builder.toString();
	}
}
