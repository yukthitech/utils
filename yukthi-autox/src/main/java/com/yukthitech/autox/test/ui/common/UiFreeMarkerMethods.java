package com.yukthitech.autox.test.ui.common;

import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Free marker methdos related to ui functionality.
 * @author akiran
 */
public class UiFreeMarkerMethods
{
	/**
	 * Used to convert strings into json strings.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Fetches the element based on specified locator. This methods
	 * helps other method to accept locator or element directly.
	 * @param locator
	 * @param parent
	 * @return
	 */
	private static WebElement getElementByLocator(Object locator, String parent)
	{
		AutomationContext context = AutomationContext.getInstance();
		WebElement webElement = null;
		
		if(locator instanceof WebElement)
		{
			webElement = (WebElement) locator;
		}
		else if(locator instanceof String)
		{
			webElement =  UiAutomationUtils.findElement(context, parent, (String)locator);
		}
		else
		{
			throw new InvalidStateException("Invalid locator/element type specified. Specified locator: {}", locator);
		}
		
		return webElement;
	}
	
	
	/**
	 * Fetches value of specified locator.
	 * @param locator locator whose value needs to be fetched
	 * @param parent parent under which locator should be searched
	 * @return locator value
	 */
	@FreeMarkerMethod("uiValue")
	public static String getValue(Object locator, String parent)
	{
		WebElement element = getElementByLocator(locator, parent);
		String elementValue = null;

		if(element == null)
		{
			elementValue = null;
		}
		else if("input".equals(element.getTagName().toLowerCase()) || "textarea".equals(element.getTagName().toLowerCase()))
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
	 * @param attrName name of attribute to fetch
	 * @param locator element locator or web-element whose attribute needs to be fetched
	 * @param parent Parent name under which locator needs to be fetched
	 * @return attribute value, if any. Otherwise null
	 */
	@FreeMarkerMethod("uiElemAttr")
	public static String getElemAttr(String attrName, Object locator, String parent)
	{
		WebElement webElement = getElementByLocator(locator, parent);
		
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
	public static boolean isVisible(Object locator, String parent)
	{
		WebElement element = getElementByLocator(locator, parent);
		return (element != null && element.isDisplayed());
	}

	/**
	 * Checks if specified element is present or not (need not be visible).
	 * @param locator locator to check.
	 * @param parent parent under which locator should be searched
	 * @return true if available (need not be visible)
	 */
	@FreeMarkerMethod("uiIsPresent")
	public static boolean isPresent(String locator, String parent)
	{
		WebElement element = getElementByLocator(locator, parent);
		return (element != null);
	}

	@FreeMarkerMethod("jsonStr")
	public static String getValue(String str)
	{
		if(str == null)
		{
			return "null";
		}
		
		//remove special charaters if any
		char chArr[] = str.toCharArray();
		
		for(int i = 0; i < chArr.length; i++)
		{
			if(Character.isWhitespace(chArr[i]))
			{
				chArr[i] = ' ';
				continue;
			}
			
			if(chArr[i] < 33 || chArr[i] > 126)
			{
				chArr[i] = ' ';
			}
		}
		
		str = new String(chArr);
		str = str.replaceAll("\\ +", " ");
		
		try
		{
			return objectMapper.writeValueAsString(str);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting intput string into json string: {}", str);
		}
	}
}
