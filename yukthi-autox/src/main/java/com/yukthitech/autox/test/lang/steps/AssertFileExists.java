package com.yukthitech.autox.test.lang.steps;

import java.io.File;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;

/**
 * Validates specified path exists.
 */
@Executable(name = "assertFileExists", group = Group.Common, message = "Validates specified path exists.")
public class AssertFileExists extends AbstractValidation
{
	private static final long serialVersionUID = 1L;
	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Path of file to check.")
	private String path;

	/**
	 * Sets the name of the data source to use.
	 *
	 * @param path the new name of the data source to use
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IValidation#execute(com.yukthitech.ui.automation.
	 * AutomationContext, com.yukthitech.ui.automation.IExecutionLogger)
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Checking for file existence: {}", path);
		
		File file = new File(path);
		
		exeLogger.debug("Found file '{}' existence status as: {}", path, file.exists());
		return file.exists();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("Path: ").append(path);

		builder.append("]");
		return builder.toString();
	}
}
