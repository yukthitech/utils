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
 * Deletes specified directory.
 * 
 * @author akiran
 */
@Executable(name = "deleteDir", group = Group.Common, message = "Deletes specified directory.")
public class DeleteDirStep extends AbstractStep
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Path of directory to delete.
	 */
	@Param(description = "Path of directory to delete.", required = true)
	private String path;

	/**
	 * Sets the path of directory to delete.
	 *
	 * @param path the new path of directory to delete
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Deleting directory: {}", path);
		
		File folder = new File(path);
		
		if(!folder.exists())
		{
			exeLogger.debug("Specified path does not exist. So ignoring delete request. Path: {}", path);
			return;
		}
		
		if(!folder.isDirectory())
		{
			exeLogger.debug("Specified path is not a directory. So ignoring delete request. Path: {}", path);
			return;
		}

		try
		{
			FileUtils.deleteDirectory(folder);
			exeLogger.debug("Successfully deleted folder - {}", path);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while deleting folder: {}", path, ex);
		}
	}
}
