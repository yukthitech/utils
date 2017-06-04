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
	public static Pattern RESOURCE_PATTERN = Pattern.compile("(\\w+)\\:(.+)", Pattern.MULTILINE);
	
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
		
		if(!parseExpressions)
		{
			return resourceObj;
		}
		
		try
		{
			String content = IOUtils.toString(resourceObj.getInputStream(), (String) null);
			content = AutomationUtils.replaceExpressions(context, content);
			
			return new StringResource(content);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while replacing expressions in resource: {}", resource, ex);
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
			
			if(TYPE_FILE.equalsIgnoreCase(resType))
			{
				if(exeLogger != null)
				{
					exeLogger.debug("Loading file as resource: {}", resValue);
				}
				
				return new FileResource(resValue);
			}
			else if(TYPE_RESOURCE.equalsIgnoreCase(resType))
			{
				if(exeLogger != null)
				{
					exeLogger.debug("Loading classpath resource: {}", resValue);
				}
				
				return new ClassPathResource(resValue);
			}
			else if(TYPE_STRING.equalsIgnoreCase(resType))
			{
				if(exeLogger != null)
				{
					exeLogger.debug("Loading specified content itself as resource: {}", resValue);
				}
				
				return new StringResource(resValue);
			}
			else if(TYPE_PROPERTY.equalsIgnoreCase(resType))
			{
				if(exeLogger != null)
				{
					exeLogger.debug("Loading context-property value as resource. Property: {}", resValue);
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
				
				return new StringResource(value.toString());
			}
			else
			{
				throw new InvalidArgumentException("Invalid resource type specified: {}", resource);
			}
		}
		else
		{
			return new StringResource(resource);
		}
	}
}
