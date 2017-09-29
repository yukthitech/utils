package com.yukthitech.autox.test.common.steps;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.resource.IResource;
import com.yukthitech.autox.resource.ResourceFactory;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.ccg.xml.util.ValidateException;

/**
 * Loads specified properties file as map on to context.
 * @author akiran
 */
@Executable(name = "loadProperties", message = "Loads specified properties file/resource as map on to context. By default the inout properties will be parsed for free marker expressions.")
public class LoadPropertiesStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Path of the resource to load.
	 */
	@Param(description = "Properties resource to load.", required = true, sourceType = SourceType.RESOURCE)
	private String resource;
	
	/**
	 * Context attribute to be used to load the map.
	 */
	@Param(description = "Name of context attribute to be used to set loaded map on context")
	private String contextAttribute;
	
	/**
	 * If true, parses input json for free marker expressions. Default: true.
	 */
	@Param(description = "If true, parses input json for free marker expressions. Default: true", required = false)
	private boolean template = true;

	/**
	 * Sets the path of the resource to load.
	 *
	 * @param resource the new path of the resource to load
	 */
	public void setResource(String resource)
	{
		this.resource = resource;
	}
	
	/**
	 * Sets the context attribute to be used to load the map.
	 *
	 * @param contextAttribute the new context attribute to be used to load the map
	 */
	public void setContextAttribute(String contextAttribute)
	{
		this.contextAttribute = contextAttribute;
	}
	
	/**
	 * Sets the if true, parses input json for free marker expressions. Default: true.
	 *
	 * @param template the new if true, parses input json for free marker expressions
	 */
	public void setTemplate(boolean template)
	{
		this.template = template;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		Properties properties = new Properties();	
		IResource resObj = ResourceFactory.getResource(context, resource, exeLogger, false);
		
		try
		{
			String propContent = IOUtils.toString(resObj.getInputStream(), (String) null);
			
			if(template)
			{
				exeLogger.debug(this, "Replacing free marker expressions in input resource");
				propContent = AutomationUtils.replaceExpressions(context, propContent);
			}
			else
			{
				exeLogger.debug(this, "Template is marked as false. Ignoring expressions in input resource");
			}

			properties.load( new ByteArrayInputStream(propContent.getBytes()) );
		}catch(Exception ex)
		{
			exeLogger.error(this, ex, "An error occurred while loading properties resource - {}.\nError: {}", resource);
			throw new TestCaseFailedException("An error occurred while loading properties resource - {}", resource, ex);
		}
		
		resObj.close();
		
		Map<String, String> resMap = new HashMap<String, String>();
		resMap.putAll((Map) properties);
		
		context.setAttribute(contextAttribute, resMap);
		return true;
	}

	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(contextAttribute))
		{
			throw new ValidateException("Context attribute can not be null or empty.");
		}
		
		if(StringUtils.isBlank(resource))
		{
			throw new ValidateException("No resource specified for loading.");
		}
	}
}
