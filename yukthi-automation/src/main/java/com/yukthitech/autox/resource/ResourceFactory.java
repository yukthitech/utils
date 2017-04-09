package com.yukthitech.autox.resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Factory of resource objects.
 * @author akiran
 */
public class ResourceFactory
{
	/**
	 * Pattern used to specify file resources.
	 */
	public static Pattern RESOURCE_PATTERN = Pattern.compile("(\\w+)\\:(.+)");
	
	/**
	 * Builds the {@link IResource} object from specified resource.
	 * @param resource resource to load
	 * @param exeLogger logger to log messages
	 * @return Resource object representing resource
	 */
	public static IResource getResource(String resource, ExecutionLogger exeLogger)
	{
		Matcher matcher = RESOURCE_PATTERN.matcher( (String) resource );
		
		if(matcher.matches())
		{
			String resType = matcher.group(1);
			
			if("file".equalsIgnoreCase(resType))
			{
				exeLogger.debug("Loading file as resource: {}", matcher.group(2));
				return new FileResource(matcher.group(2));
			}
			else if("res".equalsIgnoreCase(resType))
			{
				exeLogger.debug("Loading classpath resource: {}", matcher.group(2));
				return new ClassPathResource(matcher.group(2));
			}
			else
			{
				throw new InvalidArgumentException("Invalid resource type specified: {}", resource);
			}
		}

		return null;
	}
}
