package com.yukthitech.autox.test.common.steps;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Creates temporary file with specified content.
 * 
 * @author akiran
 */
@Executable(name = "createTempDir", group = Group.Common, message = "Creates temporary directory.")
public class CreateTempDirStep extends AbstractStep
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the attribute to use to set the generated file path.
	 */
	@Param(description = "Name of the attribute to use to set the generated file path.", required = true, attrName = true)
	private String pathAttr;

	/**
	 * Prefix to be used for generated file. Default: temp.
	 */
	@Param(description = "Prefix to be used for generated file. Default: temp", required = false)
	private String prefix = "temp";

	/**
	 * Sets the name of the attribute to use to set the generated file path.
	 *
	 * @param pathAttr the new name of the attribute to use to set the generated file path
	 */
	public void setPathAttr(String pathAttr)
	{
		this.pathAttr = pathAttr;
	}

	/**
	 * Sets the prefix to be used for generated file. Default: temp.
	 *
	 * @param prefix the new prefix to be used for generated file
	 */
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Creating temp dir with [Prefix: {}, Path attr: {}]", prefix, pathAttr);
		
		try
		{
			String tempDir = System.getProperty("java.io.tmpdir");
			
			File tempFolder = null;
			boolean found = false;
			
			for(int i = 1; i <= 10; i++)
			{
				tempFolder = new File(new File(tempDir), prefix + "-" + System.currentTimeMillis() + "-" + i);
				
				if(!tempFolder.exists())
				{
					found = true;
					break;
				}
			}
			
			if(!found)
			{
				throw new InvalidStateException("Failed to create temp directory after trying for 10 times also");
			}
			
			FileUtils.forceMkdir(tempFolder);
			
			exeLogger.debug("Created empty temp folder '{}'.", tempFolder.getPath());
			
			context.setAttribute(pathAttr, tempFolder.getPath());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating temp folder", ex);
		}
	}
}
