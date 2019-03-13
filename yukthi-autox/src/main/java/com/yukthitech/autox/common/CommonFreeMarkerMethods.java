package com.yukthitech.autox.common;

import java.io.File;
import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Common free marker methods.
 * @author akiran
 */
public class CommonFreeMarkerMethods
{
	/**
	 * Converts input file path (Can be relative, partial path) to full canonical path.
	 * @param path path to convert.
	 * @return converted path.
	 */
	@FreeMarkerMethod(
			description = "Converts input file path (Can be relative, partial path) to full canonical path.",
			returnDescription = "Canonical path of the specified path"
			)
	public static String fullPath(
			@FmParam(name = "path", description = "Path to be converted") String path)
	{
		try
		{
			return new File(path).getCanonicalPath();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An exception occurred while fetching full path of path: {}", path, ex);
		}
	}
	
	/**
	 * Fetches the value for specified xpath.
	 * @param source source on which xpath has to be fetched.
	 * @param xpath xpath to be executed.
	 * @return matching value
	 */
	@FreeMarkerMethod(
			description = "Fetches the value for specified xpath from specified source object.",
			returnDescription = "Value of specified xpath"
			)
	public static Object getValueByXpath(
			@FmParam(name = "source", description = "Source object on which xpath needs to be evaluated") Object source, 
			@FmParam(name = "xpath", description = "Xpath to be evaluated") String xpath)
	{
		return JXPathContext.newContext(source).getValue(xpath);
	}
	
	/**
	 * Fetching all the values for specified xpath.
	 * @param source source on which xpath to be fetched
	 * @param xpath xpath to be executed.
	 * @return matching values.
	 */
	@SuppressWarnings("unchecked")
	@FreeMarkerMethod(
			description = "Fetches the value(s) list for specified xpath from specified source object.",
			returnDescription = "Value of specified xpath"
			)
	public static List<Object> getValuesByXpath(
			@FmParam(name = "source", description = "Source object on which xpath needs to be evaluated") Object source, 
			@FmParam(name = "xpath", description = "Xpath to be evaluated") String xpath)
	{
		return JXPathContext.newContext(source).selectNodes(xpath);
	}

	/**
	 * Fetches the count of values obtained from specified xpath.
	 * @param source
	 * @param xpath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@FreeMarkerMethod(
			description = "Fetches the count of values matching with specified xpath from specified source object.",
			returnDescription = "Number of values matching with specified xpath"
			)
	public static int countOfXpath(
			@FmParam(name = "source", description = "Source object on which xpath should be evaluated") Object source, 
			@FmParam(name = "xpath", description = "Xpath to be evaluated") String xpath)
	{
		try
		{
			List<Object> res = JXPathContext.newContext(source).selectNodes(xpath);
			
			if(res == null)
			{
				return 0;
			}
			
			return res.size();
		}catch(JXPathNotFoundException ex)
		{
			return 0;
		}
	}

	/**
	 * Fetches value from story with specified key.
	 * @param key key to be fetched
	 * @return matching value from store
	 */
	@FreeMarkerMethod(
			description = "Fetches the value of specified key from the store.",
			returnDescription = "Matched value"
			)
	public static Object storeValue(
			@FmParam(name = "key", description = "Key of value to be fetched") String key)
	{
		return AutomationContext.getInstance().getPersistenceStorage().get(key);
	}
	
	/**
	 * Compares the specified values and returns the comparison result as int.
	 * @param value1
	 * @param value2
	 * @return
	 */
	@FreeMarkerMethod(
			description = "Compares the specified values and returns the comparision result as int.",
			returnDescription = "Comparision result."
			)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int compare(
			@FmParam(name = "value1", description = "Value1 to compare") Object value1,
			@FmParam(name = "value2", description = "Value2 to compare") Object value2
			)
	{
		if(value1 == null && value2 == null)
		{
			return 0;
		}
		
		if(value1 == value2)
		{
			return 0;
		}
		
		if(!(value1 instanceof Comparable))
		{
			throw new InvalidArgumentException("Non comparable object is specified as value1: {}", value1);
		}

		if(!(value2 instanceof Comparable))
		{
			throw new InvalidArgumentException("Non comparable object is specified as value1: {}", value1);
		}
		
		return ((Comparable)value1).compareTo(value2);
	}
	
	@FreeMarkerMethod(
			description = "Used to set value as content attribute. This function will always return empty string.",
			returnDescription = "Always empty string."
			)
	public static String setAttr(String attrName, Object value)
	{
		AutomationContext.getInstance().setAttribute(attrName, value);
		return "";
	}
}
