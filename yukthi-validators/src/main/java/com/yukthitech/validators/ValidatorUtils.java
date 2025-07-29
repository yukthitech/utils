package com.yukthitech.validators;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

import com.yukthitech.validation.IStringConvertible;

public class ValidatorUtils
{
	public static String getStrValue(Class<? extends Annotation> annotation, Object value)
	{
		if(value == null)
		{
			return null;
		}
		
		if(value instanceof String)
		{
			return (String) value;
		}
		
		if(value instanceof IStringConvertible)
		{
			return ((IStringConvertible) value).toStringValue();
		}
		
		throw new IllegalStateException(String.format("Annotation %s is used on Non-string/non-string-convertible field. Value type: %s", 
				annotation.getName(), value.getClass().getName()));
	}
	
	@SuppressWarnings("rawtypes")
	public static int getSize(Class<? extends Annotation> annotation, Object value, boolean trim)
	{
		if(value == null)
		{
			return 0;
		}
		
		if(value instanceof String)
		{
			String strValue = (String)value;
			
			if(trim)
			{
				strValue = strValue.trim();
			}
			
			return strValue.length();
		}
		
		if(value instanceof IStringConvertible)
		{
			String strValue = ((IStringConvertible)value).toStringValue();
			
			if(strValue == null)
			{
				return 0;
			}
			
			if(trim)
			{
				strValue = strValue.trim();
			}
			
			return strValue.length();
		}
		
		if(value instanceof Collection)
		{
			return ((Collection) value).size();
		}

		if(value instanceof Map)
		{
			return ((Map) value).size();
		}

		throw new IllegalStateException(String.format("Annotation %s is used on Non-string/non-string-convertible/non-collection/non-map field. Value type: %s", 
				annotation.getName(), value.getClass().getName()));
	}
}
