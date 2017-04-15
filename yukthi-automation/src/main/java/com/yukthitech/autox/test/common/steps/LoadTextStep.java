package com.yukthitech.autox.test.common.steps;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.resource.IResource;
import com.yukthitech.autox.resource.ResourceFactory;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.ccg.xml.util.ValidateException;

/**
 * Loads specified resource as text on to context.
 * @author akiran
 */
@Executable(name = "loadText", message = "Loads specified properties file/resource as text on to context")
public class LoadTextStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Path of the resource to load.
	 */
	@Param(description = "Text resource to load. Can be file or classpath resource.", required = false)
	private String resource;
	
	/**
	 * Context attribute to be used to load the map.
	 */
	@Param(description = "Name of context attribute to be used to set loaded text on context")
	private String contextAttribute;
	
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
	 * Sets the context attribute to be used to load the text.
	 *
	 * @param contextAttribute the new context attribute to be used to load the text
	 */
	public void setContextAttribute(String contextAttribute)
	{
		this.contextAttribute = contextAttribute;
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		IResource resObj = ResourceFactory.getResource(resource, exeLogger);
		String value = null;
		
		try
		{
			value = IOUtils.toString(resObj.getInputStream());
			resObj.close();
		}catch(Exception ex)
		{
			exeLogger.error(ex, "An error occurred while loading resource - {}.\nError: {}", resource);
			throw new TestCaseFailedException("An error occurred while loading resource - {}", resource, ex);
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
