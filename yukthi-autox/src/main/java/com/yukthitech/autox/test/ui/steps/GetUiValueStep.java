package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;

/**
 * Waits for locator to be part of the page and is visible.
 * @author akiran
 */
@Executable(name = "getUiValue", requiredPluginTypes = SeleniumPlugin.class, message = "Fetches value of specified ui element")
public class GetUiValueStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Locator of the element for which value needs to be fetched.
	 */
	@Param(description = "Locator of the element for which value needs to be fetched", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	/**
	 * Name of attribute to set.
	 */
	@Param(description = "Name of the attribute to set.")
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
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace(this, "Fetching ui element value for locator - {}", locator);
		
		WebElement element = UiAutomationUtils.findElement(context, null, locator);
		
		String elementValue = null;

		if(element == null)
		{
			elementValue = null;
		}
		else if("input".equals(element.getTagName().toLowerCase()))
		{
			elementValue = element.getAttribute("value").trim();
		}
		else
		{
			elementValue = element.getText().trim();
		}
		
		exeLogger.debug(this, "Setting context attribute '{}' with value of loctor '{}'. Value of locator was found to be: {}", name, locator, elementValue);
		context.setAttribute(name, elementValue);
		
		return true;
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
		builder.append(",").append("Name: ").append(name);

		builder.append("]");
		return builder.toString();
	}
}
