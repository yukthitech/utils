package com.yukthitech.autox.test.ui.steps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;

/**
 * Fill form with action fills the form and then goes for the provided action.
 * 
 * @author Pritam.
 */
@Executable(name = "uiSetValue", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Populates specified field with specified value")
public class UiSetValueStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(UiSetValueStep.class);

	/**
	 * Html locator of the form or container (like DIV) enclosing the input
	 * elements.
	 */
	@Param(description = "Locator of the element to be populated", sourceType = SourceType.UI_LOCATOR)
	private String locator;

	/**
	 * Value to be filled. 
	 */
	@Param(description = "Value to be filled with. Defaults to empty string.", required = false)
	private String value;

	/**
	 * PressEnterAtEnd if true then for the provided action or else ignore.
	 */
	@Param(description = "If true, an enter-key press will be simulated on target element after populating value. Default: false", required = false)
	private boolean pressEnterAtEnd = false;

	/**
	 * Press Enter sets the key value as enter for the web element.
	 * 
	 * @param context
	 *            current Automation context.
	 * @param exeLogger
	 *            logger.
	 */
	private void pressEnter(AutomationContext context, ExecutionLogger exeLogger)
	{
		WebElement webElement = UiAutomationUtils.findElement(context, parentElement, locator);
		webElement.sendKeys(Keys.ENTER);

		logger.debug("Successfully enter key is pressed");
	}

	/**
	 * Loops throw the properties specified data bean and populates the fields
	 * with matching names.
	 * 
	 * @param context
	 *            Current automation context
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger logger)
	{
		logger.debug("Populating field {} with value - {}", getLocatorWithParent(locator), value);
		
		if(value == null)
		{
			value = "";
		}
		
		if(!UiAutomationUtils.populateField(context, parentElement, locator, value))
		{
			logger.error("Failed to fill element '{}' with value - {}", getLocatorWithParent(locator), value);
			throw new TestCaseFailedException(this, "Failed to fill element '{}' with value - {}", getLocatorWithParent(locator), value);
		}

		if(pressEnterAtEnd)
		{
			logger.debug( "User has provided enter key to be pressed");

			context.getStepListenerProxy().stepPhase(this, "Pressing enter");
			pressEnter(context, logger);
		}
		
		return true;
	}

	/**
	 * Gets the html locator of the form or container (like DIV) enclosing the
	 * input elements.
	 *
	 * @return the html locator of the form or container (like DIV) enclosing
	 *         the input elements
	 */
	public String getLocator()
	{
		return locator;
	}

	/**
	 * Sets the html locator of the form or container (like DIV) enclosing the
	 * input elements.
	 *
	 * @param locator
	 *            the new html locator of the form or container (like DIV)
	 *            enclosing the input elements
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/**
	 * Gets the value to be filled.
	 *
	 * @return the value to be filled
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value to be filled.
	 *
	 * @param value the new value to be filled
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Gets press enter at end.
	 * 
	 * @return boolean value of pressEnterAtTheEnd.
	 */
	public boolean getPressEnterAtEnd()
	{
		return pressEnterAtEnd;
	}

	/**
	 * Sets value for press enter at the end.
	 * 
	 * @param pressEnterAtEnd
	 *            the new press enter at the end.
	 */
	public void setPressEnterAtEnd(boolean pressEnterAtEnd)
	{
		this.pressEnterAtEnd = pressEnterAtEnd;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Set Value [");

		builder.append("Locator: ").append(locator);
		builder.append(",").append("Value: ").append(value);

		builder.append("]");
		return builder.toString();
	}

}
