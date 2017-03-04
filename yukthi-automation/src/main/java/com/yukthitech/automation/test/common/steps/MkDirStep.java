package com.yukthitech.automation.test.common.steps;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.test.TestCaseFailedException;

/**
 * Creates a directory with required parent folder as needed in work folder.
 * 
 * @author akiran
 */
@Executable(name = "mkdir", message = "Creates a directory with required parent folder as needed in work folder.")
public class MkDirStep implements IStep
{
	/**
	 * Directory path to create.
	 */
	@Param(description = "Directory path to create.")
	private String path;

	/**
	 * Context attribute to which result folder path will be set.
	 */
	@Param(description = "Context attribute to which result folder path will be set")
	private String contextAttribute;

	/**
	 * Sets the directory path to create.
	 *
	 * @param path the new directory path to create
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * Sets the context attribute to which result folder path will be set.
	 *
	 * @param contextAttribute the new context attribute to which result folder path will be set
	 */
	public void setContextAttribute(String contextAttribute)
	{
		this.contextAttribute = contextAttribute;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.IStep#execute(com.yukthitech.automation.AutomationContext, com.yukthitech.automation.IExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Creating directory {} in work folder: {}", path, context.getWorkDirectory().getPath());
		
		File dirToCreate = new File(context.getWorkDirectory().getPath() + File.separator + path);
		
		try
		{
			FileUtils.forceMkdir(dirToCreate);
			
			exeLogger.debug("Setting created directory path {} on context with attribute - {}", dirToCreate, contextAttribute);
			
			context.setAttribute(contextAttribute, dirToCreate);
		}catch(Exception ex)
		{
			exeLogger.error(ex, "An error occurred while creating directory - {}", path);
			throw new TestCaseFailedException("An error occurred while creating directory - {}", path, ex);
		}
		
	}
}
