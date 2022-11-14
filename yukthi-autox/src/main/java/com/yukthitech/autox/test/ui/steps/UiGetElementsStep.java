package com.yukthitech.autox.test.ui.steps;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;

/**
 * Waits for locator to be part of the page and is visible.
 * @author akiran
 */
@Executable(name = "uiGetElements", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Fetches value of specified ui element")
public class UiGetElementsStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Locator of the element for which value needs to be fetched.
	 */
	@Param(description = "Locator of the elements to be fetched", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	/**
	 * Name of attribute to set.
	 */
	@Param(description = "Name of the attribute to set.", attrName = true)
	private String name;

	/**
	 * Sets the locator of the element for which value needs to be fetched.
	 *
	 * @param locator the new locator of the element for which value needs to be fetched
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/**
	 * Sets the name of attribute to set.
	 *
	 * @param name the new name of attribute to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Fetching ui element value for locator - {}", locator);
		
		
		List<WebElement> webElements = UiAutomationUtils.findElements(context, parentElement, locator);
		
		if(webElements == null || webElements.isEmpty())
		{
			exeLogger.debug("No webelements found with locator: {}", getLocatorWithParent(locator));
			context.setAttribute(name, new ArrayList<>());
			
			return;
		}
		
		exeLogger.debug("Setting context attribute '{}' with webelements [count: {}] matching locator: {}", 
				name, webElements.size(), getLocatorWithParent(locator));
		context.setAttribute(name, webElements);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Get Elements [");

		builder.append("Locator: ").append(locator);
		builder.append(",").append("Name: ").append(name);

		builder.append("]");
		return builder.toString();
	}
}
