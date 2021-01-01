package com.yukthitech.autox.test.ui.common;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.CustomUiLocator;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
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
	@FreeMarkerMethod(
			description = "Fetches value of specified locator. If element is text/textarea, its ui value will be fetched.",
			returnDescription = "Value of the ui element"
			)
	public static String uiValue(
			@FmParam(name = "locator", description = "Locator of the ui element whose element needs to be fetched.") Object locator, 
			@FmParam(name = "parent", description = "Optional. Context attribute name which should hold parent web element.") String parent)
	{
		ObjectWrapper<String> queryWrapper = new ObjectWrapper<String>();
		CustomUiLocator customUiLocator = UiAutomationUtils.getCustomUiLocator(AutomationContext.getInstance(), locator.toString(), queryWrapper);
		
		if(customUiLocator != null)
		{
			return customUiLocator.getValue(queryWrapper.getValue(), false);
		}

		WebElement element = getElementByLocator(locator, parent);

		if(element == null)
		{
			return null;
		}
		
		FormFieldType fieldType = UiAutomationUtils.getFormFieldType(element);
		
		if(fieldType != null)
		{
			return fieldType.getFieldAccessor().getValue(AutomationContext.getInstance(), element);
		}
		
		return element.getText().trim();
	}
	
	/**
	 * Fetches display value of specified locator.
	 * @param locator locator whose value needs to be fetched
	 * @param parent parent under which locator should be searched
	 * @return locator value
	 */
	@FreeMarkerMethod(
			description = "Fetches display value of specified locator. For select, option label will be fetched. If element is not Select, its ui value will be fetched.",
			returnDescription = "Value of the ui element"
			)
	public static String uiDisplayValue(
			@FmParam(name = "locator", description = "Locator of the ui element whose display value needs to be fetched.") Object locator, 
			@FmParam(name = "parent", description = "Optional. Context attribute name which should hold parent web element.") String parent)
	{
		ObjectWrapper<String> queryWrapper = new ObjectWrapper<String>();
		CustomUiLocator customUiLocator = UiAutomationUtils.getCustomUiLocator(AutomationContext.getInstance(), locator.toString(), queryWrapper);
		
		if(customUiLocator != null)
		{
			return customUiLocator.getValue(queryWrapper.getValue(), true);
		}


		WebElement element = getElementByLocator(locator, parent);

		if(element == null)
		{
			return null;
		}
		
		FormFieldType fieldType = UiAutomationUtils.getFormFieldType(element);
		
		if(fieldType != null)
		{
			return fieldType.getFieldAccessor().getDisplayValue(AutomationContext.getInstance(), element);
		}
		
		return element.getText().trim();
	}

	/**
	 * Fetches the specified attribute value of specified element.
	 * @param attrName name of attribute to fetch
	 * @param locator element locator or web-element whose attribute needs to be fetched
	 * @param parent Parent name under which locator needs to be fetched
	 * @return attribute value, if any. Otherwise null
	 */
	@FreeMarkerMethod(
			description = "Fetches attribute value of specified locator.",
			returnDescription = "Value of the ui element attribute"
			)
	public static String uiElemAttr(
			@FmParam(name = "attrName", description = "Name of the attribute whose value to be fetched.") String attrName, 
			@FmParam(name = "locator", description = "Locator of the ui element whose attribute value needs to be fetched.") Object locator, 
			@FmParam(name = "parent", description = "Optional. Context attribute name which should hold parent web element.") String parent)
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
	@FreeMarkerMethod(
			description = "Checks if specified element is visible or not.",
			returnDescription = "True if element is visible"
			)
	public static boolean uiIsVisible(
			@FmParam(name = "locator", description = "Locator of the ui element whose attribute value needs to be fetched.") Object locator, 
			@FmParam(name = "parent", description = "Optional. Context attribute name which should hold parent web element.") String parent)
	{
		try
		{
			WebElement element = getElementByLocator(locator, parent);
			return (element != null && element.isDisplayed());
		}catch(StaleElementReferenceException ex)
		{
			return false;
		}
	}

	/**
	 * Checks if specified element is present or not (need not be visible).
	 * @param locator locator to check.
	 * @param parent parent under which locator should be searched
	 * @return true if available (need not be visible)
	 */
	@FreeMarkerMethod(
			description = "Checks if specified element is present or not (need not be visible).",
			returnDescription = "True if element is available (need not be visible)"
			)
	public static boolean uiIsPresent(
			@FmParam(name = "locator", description = "Locator of the ui element whose attribute value needs to be fetched.") String locator, 
			@FmParam(name = "parent", description = "Optional. Context attribute name which should hold parent web element.") String parent)
	{
		WebElement element = getElementByLocator(locator, parent);
		return (element != null);
	}

	@FreeMarkerMethod(
			description = "Removes special characters and coverts result into json string (enclosed in double quotes)",
			returnDescription = "Converted string"
			)
	public static String escape(
			@FmParam(name = "str", description = "String to be converted") String str)
	{
		if(str == null)
		{
			return "null";
		}
		
		//remove special characters if any
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

	@FreeMarkerMethod(
			description = "Fetches the size of the browser",
			returnDescription = "Size of the browser"
			)
	public static Dimension uiBrowserSize()
	{
		AutomationContext context = AutomationContext.getInstance();
		SeleniumPlugin plugin = context.getPlugin(SeleniumPlugin.class);
		
		return plugin.getWebDriver().manage().window().getSize();
	}

	@FreeMarkerMethod(
			description = "Fetches the position of the browser",
			returnDescription = "Position of the browser"
			)
	public static Point uiBrowserPosition()
	{
		AutomationContext context = AutomationContext.getInstance();
		SeleniumPlugin plugin = context.getPlugin(SeleniumPlugin.class);
		
		return plugin.getWebDriver().manage().window().getPosition();
	}
}
