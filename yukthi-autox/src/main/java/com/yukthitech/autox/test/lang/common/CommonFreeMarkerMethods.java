package com.yukthitech.autox.test.lang.common;

import java.io.File;
import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;

import com.yukthitech.utils.exceptions.InvalidStateException;
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
	@FreeMarkerMethod("fullPath")
	public static String fullPath(String path)
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
	@FreeMarkerMethod("getValueByXpath")
	public static Object getValueByXpath(Object source, String xpath)
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
	@FreeMarkerMethod("getValuesByXpath")
	public static List<Object> getValuesByXpath(Object source, String xpath)
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
	@FreeMarkerMethod("countOfXpath")
	public static int countOfXpath(Object source, String xpath)
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
}
