package com.yukthitech.webutils.common;

import java.lang.reflect.Method;

/**
 * Contains common util methods required by webutils.
 * @author akiran
 */
public class WebutilsCommonUtils
{
	/**
	 * Generates bare minimal method signature that can be used to identify method uniquely.
	 * @param method Method for which signature needs to be generated
	 * @return Bare minimal method signature
	 */
	public static String getMethodSignature(Method method)
	{
		StringBuilder builder = new StringBuilder(method.getName());
		
		builder.append("(");
		
		Class<?> paramTypes[] = method.getParameterTypes();
		
		if(paramTypes.length > 0)
		{
			for(Class<?> ptype : paramTypes)
			{
				if(ptype.getName().contains("MultipartHttpServletRequest"))
				{
					ptype = Object.class;
				}
				
				builder.append(ptype.getName()).append(",");
			}
			
			builder.deleteCharAt(builder.length() - 1);
		}
		
		builder.append(")");
		return builder.toString();
	}
}
