package com.yukthitech.autox.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.resource.IResource;
import com.yukthitech.autox.resource.ResourceFactory;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Constants that will be used across the classes.
 * @author akiran
 */
public interface IAutomationConstants
{
	/**
	 * Object mapper for parsing and formatting json.
	 */
	public ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	/**
	 * Pattern used to refer property in the source.
	 */
	public Pattern REF_PATTERN = Pattern.compile("ref\\:(.+)");
	
	/**
	 * Parses specified source (if string) and returns the result. If not string is specified, the 
	 * same will be returned.
	 * @param exeLogger Logger for logging messages
	 * @param source source to be passed
	 * @return parsed value
	 */
	public static Object PARSE_OBJ_SOURCE(AutomationContext context, ExecutionLogger exeLogger, Object source)
	{
		if(!(source instanceof String))
		{
			return source;
		}
		
		String sourceStr = (String) source;
		sourceStr = sourceStr.trim();
		
		//check if string is a reference
		Matcher matcher = REF_PATTERN.matcher(sourceStr);
		
		if(matcher.matches())
		{
			try
			{
				return PropertyUtils.getProperty(context, matcher.group(1));
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while evaluating expression {} on context", matcher.group(1));
			}
		}

		//check if string is resource
		IResource resource = ResourceFactory.getResource(context, sourceStr, exeLogger, true);
		
		try
		{
			Object value = OBJECT_MAPPER.readValue(resource.getInputStream(), Object.class);
			resource.close();
			
			return value;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An exception occurred while parsing json resource: " + sourceStr, ex);
		}
	}
}
