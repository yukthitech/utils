package com.yukthitech.autox.resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Factory of resource objects.
 * @author akiran
 */
public class ResourceFactory
{
	/**
	 * Pattern used to specify file resources.
	 */
	public static Pattern RESOURCE_PATTERN = Pattern.compile("([\\-\\w]+)\\:(.+)", Pattern.MULTILINE | Pattern.DOTALL);
	
	/**
	 * Prefix that will be used for raw resources.
	 */
	public static final String RAW_RES_PREFIX = "raw-";
	
	/**
	 * value type is property.
	 */
	public static final String TYPE_PROPERTY = "property";

	/**
	 * value type is property.
	 */
	public static final String TYPE_FILE = "file";

	/**
	 * value type is property.
	 */
	public static final String TYPE_RESOURCE = "res";

	/**
	 * value type is property.
	 */
	public static final String TYPE_STRING = "string";

	public static IResource getResource(AutomationContext context, String resource, ExecutionLogger exeLogger, boolean parseExpressions)
	{
		IResource resourceObj = getResource(context, resource, exeLogger);
		
		if(!parseExpressions || resourceObj.isRawType())
		{
			return resourceObj;
		}

		return parseExpressions(context, resourceObj);
	}
	
	public static IResource parseExpressions(AutomationContext context, IResource resourceObj)
	{
		try
		{
			String content = IOUtils.toString(resourceObj.getInputStream(), (String) null);
			content = AutomationUtils.replaceExpressionsInString("content", context, content);
			
			return new StringResource(resourceObj.getName(), content, false);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while replacing expressions in resource: {}", resourceObj, ex);
		}
	}
	
	public static IResource getResource(AutomationContext context, ExecutionLogger exeLogger, String resType, String resValue)
	{
		if(resType == null)
		{
			return new StringResource(resValue, false);
		}
		
		boolean rawType = false;
		
		if(resType.toLowerCase().startsWith(RAW_RES_PREFIX))
		{
			resType = resType.substring(RAW_RES_PREFIX.length());
			rawType = true;
		}
		
		if(TYPE_FILE.equalsIgnoreCase(resType))
		{
			if(exeLogger != null)
			{
				exeLogger.debug(null, "Loading file as resource: {}", resValue);
			}
			
			return new FileResource(resValue, rawType);
		}
		else if(TYPE_RESOURCE.equalsIgnoreCase(resType))
		{
			if(exeLogger != null)
			{
				exeLogger.debug(null, "Loading classpath resource: {}", resValue);
			}
			
			return new ClassPathResource(resValue, rawType);
		}
		else if(TYPE_STRING.equalsIgnoreCase(resType))
		{
			if(exeLogger != null)
			{
				exeLogger.debug(null, "Loading specified content itself as resource: {}", resValue);
			}
			
			return new StringResource(resValue, rawType);
		}
		else if(TYPE_PROPERTY.equalsIgnoreCase(resType))
		{
			if(exeLogger != null)
			{
				exeLogger.debug(null, "Loading context-property value as resource. Property: {}", resValue);
			}
			
			Object value = null;
			
			try
			{
				value = PropertyUtils.getProperty(context, resValue);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while fetching context property: {}", resValue, ex);
			}
			
			if(value == null)
			{
				throw new InvalidStateException("Got property value as null. Property:  " + resValue);
			}
			
			return new StringResource(value.toString(), rawType);
		}
		else
		{
			throw new InvalidArgumentException("Invalid resource type specified: {}", resType);
		}
	}
	
	/**
	 * Builds the {@link IResource} object from specified resource.
	 * @param resource resource to load
	 * @param exeLogger logger to log messages
	 * @return Resource object representing resource
	 */
	private static IResource getResource(AutomationContext context, String resource, ExecutionLogger exeLogger)
	{
		Matcher matcher = RESOURCE_PATTERN.matcher( (String) resource );
		
		if(matcher.matches())
		{
			String resType = matcher.group(1);
			String resValue = matcher.group(2);
		
			return getResource(context, exeLogger, resType, resValue);
		}
		
		return new StringResource(resource, false);
	}
}
