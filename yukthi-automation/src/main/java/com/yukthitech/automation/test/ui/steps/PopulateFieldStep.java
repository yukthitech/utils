package com.yukthitech.automation.test.ui.steps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.yukthitech.automation.AbstractStep;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.config.SeleniumPlugin;
import com.yukthitech.automation.test.TestCaseFailedException;
import com.yukthitech.automation.test.ui.common.UiAutomationUtils;

/**
 * Fill form with action fills the form and then goes for the provided action.
 * 
 * @author Pritam.
 */
@Executable(name = "populateField", requiredPluginTypes = SeleniumPlugin.class, message = "Populates specified field with specified message")
public class PopulateFieldStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(PopulateFieldStep.class);

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
	private void pressEnter(AutomationContext context, ExecutionLogger exeLogger)
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
	public void execute(AutomationContext context, ExecutionLogger logger)
	{
		logger.debug("Populating field {} with value - {}", locator, value);
		
		if(!UiAutomationUtils.populateField(context, null, locator, value))
		{
			logger.error("Failed to fill element '{}' with value - {}", locator, value);
			throw new TestCaseFailedException("Failed to fill element '{}' with value - {}", locator, value);
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
