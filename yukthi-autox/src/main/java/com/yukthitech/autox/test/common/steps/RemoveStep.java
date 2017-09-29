package com.yukthitech.autox.test.common.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;

/**
 * Removes the specified context attribute.
 * 
 * @author akiran
 */
@Executable(name = "remove", message = "Removes the specified context attribute.")
public class RemoveStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of attribute to set.
	 */
	@Param(description = "Name of the attribute to set.")
	private String name;

	/**
	 * Sets the name of attribute to set.
	 *
	 * @param name the new name of attribute to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug(this, "Removing context attribute '{}'", name);
		context.removeAttribute(name);
		return true;
	}
}
