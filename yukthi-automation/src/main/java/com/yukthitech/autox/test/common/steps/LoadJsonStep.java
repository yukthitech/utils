package com.yukthitech.autox.test.common.steps;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.resource.IResource;
import com.yukthitech.autox.resource.ResourceFactory;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.ccg.xml.util.ValidateException;

/**
 * Loads specified json resource as object on to context.
 * @author akiran
 */
@Executable(name = "loadJson", message = "Loads specified json file/resource as json on to context. By default the inout json will be parsed for free marker expressions.")
public class LoadJsonStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Path of the resource to load.
	 */
	@Param(description = "Json resource to load. Can be file or classpath resource.", required = false)
	private String resource;
	
	/**
	 * Context attribute to be used to load the map.
	 */
	@Param(description = "Name of context attribute to be used to set loaded json on context")
	private String contextAttribute;
	
	/**
	 * If true, parses input json for free marker expressions. Default: true.
	 */
	@Param(description = "If true, parses input json for free marker expressions. Default: true", required = false)
	private boolean template = true;
	
	/**
	 * Type of bean to which json should be parsed to. Default: Object, that is json will be parsed to map of maps.
	 */
	@Param(description = "Type of bean to which json should be parsed to. Default: Object, that is json will be parsed to map of maps", required = false)
	private String type;
	
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
	 * Sets the context attribute to be used to load the json.
	 *
	 * @param contextAttribute the new context attribute to be used to load the json
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
	
	/**
	 * Sets the type of bean to which json should be parsed to. Default: Object, that is json will be parsed to map of maps.
	 *
	 * @param type the new type of bean to which json should be parsed to
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		IResource resObj = ResourceFactory.getResource(resource, exeLogger);
		
		if(resObj == null)
		{
			throw new TestCaseFailedException("Invalid resource specified: {}", resource);
		}

		Object value = null;
		
		Class<?> resType = Object.class;
		
		if(this.type != null)
		{
			try
			{
				resType = Class.forName(type);
			}catch(Exception ex)
			{
				exeLogger.error(ex, "An error occurred while loading specified type: {}", type);
				throw new TestCaseFailedException("An error occurred while loading specified type: {}", type, ex);
			}
		}
		
		try
		{
			String jsonContent = IOUtils.toString(resObj.getInputStream());
			
			if(template)
			{
				exeLogger.debug("Replacing free marker expressions in input resource");
				jsonContent = AutomationUtils.replaceExpressions(context, jsonContent);
			}
			else
			{
				exeLogger.debug("Template is marked as false. Ignoring expressions in input resource");
			}
			
			value = IAutomationConstants.OBJECT_MAPPER.readValue(jsonContent, resType);
			resObj.close();
		}catch(Exception ex)
		{
			exeLogger.error(ex, "An error occurred while loading json resource - {}.\nError: {}", resource);
			throw new TestCaseFailedException("An error occurred while loading json resource - {}", resource, ex);
		}
		
		context.setAttribute(contextAttribute, value);
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
