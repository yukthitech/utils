package com.yukthitech.autox.test.lang.steps;

import java.io.File;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IExecutionLogger;
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

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Checking for file existence: {}", path);
		
		File file = new File(path);
		
		exeLogger.debug("Found file '{}' existence status as: {}", path, file.exists());
		
		if(!file.exists())
		{
			throw new AutoxValidationException(this, "Found file does not exist: {}", path);
		}
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
