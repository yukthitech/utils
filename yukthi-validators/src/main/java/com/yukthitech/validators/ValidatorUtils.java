package com.yukthitech.validators;

import java.lang.annotation.Annotation;

import com.yukthitech.validation.IStringConvertible;

public class ValidatorUtils
{
	public static String getValue(Class<? extends Annotation> annotation, Object value)
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
}
