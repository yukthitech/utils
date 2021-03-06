package com.yukthitech.autox.test.ui.steps;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.autox.test.ui.steps.AbstractUiStep;

/**
 * Simulates the click event on the specified button.
 * 
 * @author akiran
 */
@Executable(name = "uiSetStyle", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Used to manipulate the style of the element.")
public class SetStyleStep extends AbstractUiStep
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Locator of the element whose style needs to be modified.
	 */
	@Param(description = "Locator of the element whose style needs to be modified.", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	/**
	 * Styles to be modified.
	 */
	@Param(description = "Styles to be modified.")
	private Map<String, String> styles = new HashMap<>();
	
	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace("On locator '{}' setting styles: {}", locator, styles);

		WebElement webElement = UiAutomationUtils.findElement(context, super.parentElement, locator);

		if(webElement == null)
		{
			exeLogger.error("Failed to find element with locator: {}", getLocatorWithParent(locator));
			throw new NullPointerException("Failed to find element with locator: " + getLocatorWithParent(locator));
		}

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
		String code = null;

		for(String style: styles.keySet())
		{
			code = "arguments[0].style['" + style + "']=" + styles.get(style);
			exeLogger.debug("Executing js code: {}", code);
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(code, webElement);
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

	public void addStyle(String name, String value)
	{
		this.styles.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Set Style [");

		builder.append("Locator: ").append(locator);

		builder.append("]");
		return builder.toString();
	}
}
