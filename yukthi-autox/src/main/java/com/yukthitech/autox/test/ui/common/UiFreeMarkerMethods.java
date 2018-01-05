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
	 * @param parent parent under which locator should be searched
	 * @return locator value
	 */
	@FreeMarkerMethod("uiValue")
	public static String getValue(String locator, String parent)
	{
		AutomationContext context = AutomationContext.getInstance();
		
		WebElement element = UiAutomationUtils.findElement(context, parent, locator);
		
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
	 * Fetches the specified attribute value of specified element.
	 * @param elementName Context attribute name of the element whose attribute needs to be fetched
	 * @param attrName name of attribute to fetch
	 * @return attribute value, if any. Otherwise null
	 */
	@FreeMarkerMethod("uiElemAttr")
	public static String getElemAttr(String elementName, String attrName)
	{
		AutomationContext context = AutomationContext.getInstance();
		WebElement webElement = (WebElement) context.getAttribute(elementName);
		
		if(webElement == null)
		{
			return null;
		}
		
		return webElement.getAttribute(attrName);
	}

	/**
	 * Checks if specified element is visible or not.
	 * @param locator locator to check.
	 * @param parent parent under which locator should be searched
	 * @return true if available and visible
	 */
	@FreeMarkerMethod("uiIsVisible")
	public static boolean isVisible(String locator, String parent)
	{
		AutomationContext context = AutomationContext.getInstance();

		WebElement element = UiAutomationUtils.findElement(context, parent, locator);
		return (element != null && element.isDisplayed());
	}
}
