package com.yukthitech.automation.test.ui.steps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.config.SeleniumConfiguration;
import com.yukthitech.automation.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Fill form with action fills the form and then goes for the provided action.
 * 
 * @author Pritam.
 */
@Executable(value = "populateField", requiredConfigurationTypes = SeleniumConfiguration.class, message = "Populates specified field with specified message")
public class PopulateFieldStep implements IStep
{
	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(PopulateFieldStep.class);

	/**
	 * The error message.
	 **/
	private static String DEBUG_MESSAGE = "Populating field {} with value - {}";

	/**
	 * The error message.
	 **/
	private static String ERROR_MESSAGE = "Failed to fill element '{}' with value - {}";

	/**
	 * Html locator of the form or container (like DIV) enclosing the input
	 * elements.
	 */
	private String locator;

	/**
	 * Value to be filled. 
	 */
	private String value;

	/**
	 * PressEnterAtEnd if true then for the provided action or else ignore.
	 */
	private boolean pressEnterAtEnd;

	/**
	 * Press Enter sets the key value as enter for the web element.
	 * 
	 * @param context
	 *            current Automation context.
	 * @param exeLogger
	 *            logger.
	 */
	private void pressEnter(AutomationContext context, IExecutionLogger exeLogger)
	{
		WebElement webElement = UiAutomationUtils.findElement(context, null, locator);
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
	public void execute(AutomationContext context, IExecutionLogger logger)
	{
		logger.debug(DEBUG_MESSAGE, locator, value);
		
		if(!UiAutomationUtils.populateField(context, null, locator, value))
		{
			logger.error(ERROR_MESSAGE, locator, value);
			throw new InvalidStateException(ERROR_MESSAGE, locator, value);
		}

		if(pressEnterAtEnd)
		{
			logger.debug("User has provided enter key to be pressed");

			pressEnter(context, logger);
		}
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
}
