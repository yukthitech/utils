package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Returns from current step group execution.
 * @author akiran
 */
@Executable(name = "return", group = Group.Lang, message = "Returns from current execution. Currently this is supported only in step-group.")
public class ReturnStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Value to be returned.
	 */
	@Param(description = "Value to be returned.", required = false, sourceType = SourceType.EXPRESSION)
	private Object value;
	
	/**
	 * Sets the value to be returned.
	 *
	 * @param value the new value to be returned
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		exeLogger.debug("Returning from current execution");
		throw new ReturnException(value);
	}
}
