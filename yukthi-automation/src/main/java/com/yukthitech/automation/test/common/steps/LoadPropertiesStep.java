package com.yukthitech.automation.test.common.steps;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.Param;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Loads specified properties file as map on to context.
 * @author akiran
 */
@Executable(name = "loadProperties", message = "Loads specified properties file/resource as map on to context")
public class LoadPropertiesStep implements IStep, Validateable
{
	/**
	 * Holds the path of file.
	 */
	@Param(description = "Properties file to load. Either file or resource is mandatory.", required = false)
	private String file;
	
	/**
	 * Path of the resource to load.
	 */
	@Param(description = "Properties resource to load. Either file or resource is mandatory.", required = false)
	private String resource;
	
	/**
	 * Context attribute to be used to load the map.
	 */
	@Param(description = "Name of context attribute to be used to set loaded map on context")
	private String contextAttribute;
	
	/**
	 * Set the file path name.
	 * 
	 * @param filePathName new file path name.
	 */
	public void setFile(String filePathName) 
	{
		this.file = filePathName;
	}
	
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
	 * Loads the file into specified properties.
	 * @param properties properties to load
	 * @param exeLogger logger
	 */
	private void loadFile(Properties properties, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Loading properties file {} to context attribute - {}", file, contextAttribute);
		
		FileInputStream fileInputStream = null;
		
		try
		{
			fileInputStream = new FileInputStream(file);
			properties.load(fileInputStream);
			
			fileInputStream.close();
		}catch(Exception ex)
		{
			exeLogger.error("An error occurred while loading properties file - {}.\nError: {}", file, ex);
			throw new InvalidStateException(ex, "An error occurred while loading properties file - {}", file);
		}
	}

	/**
	 * Loads the resource into specified properties.
	 * @param properties properties to load
	 * @param exeLogger logger
	 */
	private void loadResource(Properties properties, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Loading properties resource {} to context attribute - {}", resource, contextAttribute);
		
		InputStream is = null;
		
		try
		{
			is = LoadPropertiesStep.class.getResourceAsStream(resource);
			properties.load(is);
			
			is.close();
		}catch(Exception ex)
		{
			exeLogger.error("An error occurred while loading properties resource - {}.\nError: {}", resource, ex);
			throw new InvalidStateException(ex, "An error occurred while loading properties resource - {}", resource);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		Properties properties = new Properties();	
		
		if(file != null)
		{
			loadFile(properties, exeLogger);
		}
		else
		{
			loadResource(properties, exeLogger);
		}
		
		Map<String, String> resMap = new HashMap<String, String>();
		resMap.putAll((Map) properties);
		
		context.setAttribute(contextAttribute, resMap);
	}

	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(contextAttribute))
		{
			throw new ValidateException("Context attribute can not be null or empty.");
		}
		
		if(StringUtils.isBlank(file) && StringUtils.isBlank(resource))
		{
			throw new ValidateException("Either of resource or file is mandatory.");
		}
	}
}
