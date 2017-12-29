package com.yukthitech.autox.test.ui.common;

import org.openqa.selenium.WebElement;

import com.yukthi.utils.fmarker.annotaion.FreeMarkerMethod;
import com.yukthitech.autox.AutomationContext;

/**
 * Free marker methdos related to ui functionality.
 * @author akiran
 */
public class UiFreeMarkerMethods
{
	/**
	 * Fetches value of specified locator.
	 * @param locator locator whose value needs to be fetched
	 * @return locator value
	 */
	@FreeMarkerMethod
	public static String getValue(String locator)
	{
		AutomationContext context = AutomationContext.getInstance();
		
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
		
		return elementValue;
	}

	/**
	 * Checks if specified element is visible or not.
	 * @param locator locator to check.
	 * @return true if available and visible
	 */
	@FreeMarkerMethod
	public static boolean isVisible(String locator)
	{
		AutomationContext context = AutomationContext.getInstance();

		WebElement element = UiAutomationUtils.findElement(context, null, locator);
		return (element != null && element.isDisplayed());
	}
}
