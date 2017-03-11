package com.yukthitech.automation.test.ui.validations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;

import com.yukthitech.automation.AbstractValidation;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.config.SeleniumPlugin;
import com.yukthitech.automation.test.ui.common.UiAutomationUtils;

/**
 * Waits for locator to be part of the page and is visible.
 * 
 * @author akiran
 */
@Executable(name = "validateValue", requiredPluginTypes = SeleniumPlugin.class, message = "Validates specified element has specified value/text")
public class ValidateValue extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(ValidateValue.class);

	/**
	 * locator of which value needs to be validated.
	 */
	private String locator;

	/**
	 * Value expected in the targer element.
	 */
	private String value;

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
	public boolean validate(AutomationContext context, ExecutionLogger exeLogger)
	{
		logger.trace("Validating if locator '{}' has value - {}", locator, value);
		
		WebElement element = UiAutomationUtils.findElement(context, null, locator);
		String actualMessage = null;
		
		if("input".equals(element.getTagName().toLowerCase()))
		{
			actualMessage = element.getAttribute("value").trim();
		}
		else
		{
			actualMessage = element.getText().trim();
		}

		if(!value.equals(actualMessage))
		{
			exeLogger.error("Expected value '{}' is not matching with actual value '{}' for locator - {}", value, actualMessage, locator);
			return false;
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
	 * Gets the value expected in the targer element.
	 *
	 * @return the value expected in the targer element
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value expected in the targer element.
	 *
	 * @param value the new value expected in the targer element
	 */
	public void setValue(String value)
	{
		this.value = value;
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
		builder.append(comma).append("Value: ").append(value);

		builder.append("]");
		return builder.toString();
	}
}
