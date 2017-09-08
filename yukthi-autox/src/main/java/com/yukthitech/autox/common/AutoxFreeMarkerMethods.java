package com.yukthitech.autox.common;

import com.yukthi.utils.fmarker.annotaion.FreeMarkerMethod;
import com.yukthitech.autox.AutomationContext;

/**
 * Default free marker methods.
 * @author akiran
 */
public class AutoxFreeMarkerMethods
{
	/**
	 * Converts specified attribute value into string.
	 * @param attrName name of attribute to convert.
	 * @return string representation of attribute value.
	 */
	@FreeMarkerMethod
	public static String attrToString(String attrName)
	{
		if(attrName == null)
		{
			return "null";
		}
		
		Object object = AutomationContext.getInstance().getAttribute(attrName);
		
		if(object == null)
		{
			return "null";
		}
		
		return object.toString();
	}
}
