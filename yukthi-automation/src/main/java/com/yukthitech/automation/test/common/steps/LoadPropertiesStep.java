package com.yukthitech.automation.test.common.steps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Loads specified properties file as map on to context.
 * @author akiran
 */
@Executable(name = "loadProperties", message = "Loads specified properties file as map on to context")
public class LoadPropertiesStep implements IStep 
{
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(LoadPropertiesStep.class);
	
	/**
	 * Holds the address of file.
	 */
	private String file;
	
	/**
	 * Context attribute to be used to load the map.
	 */
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
	 * Sets the context attribute to be used to load the map.
	 *
	 * @param contextAttribute the new context attribute to be used to load the map
	 */
	public void setContextAttribute(String contextAttribute)
	{
		this.contextAttribute = contextAttribute;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		File file = new File(this.file);
		FileInputStream fileInputStream = null;
		
		try
		{
			fileInputStream = new FileInputStream(file);
		}catch(FileNotFoundException ex)
		{
			throw new InvalidStateException(ex, "File not found in the provided location - {}", file);
		}
		
		Properties properties = new Properties();
		
		try
		{
			properties.load(fileInputStream);
		}catch(IOException ex)
		{
			throw new InvalidStateException(ex, "An error occured while loading the properties file");
		}
		
		Map<String, String> resMap = new HashMap<String, String>();
		resMap.putAll((Map) properties);
		
		context.setAttribute(contextAttribute, resMap);
	}
}
