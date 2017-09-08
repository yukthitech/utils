package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;

/**
 * Returns from current step group execution.
 * @author akiran
 */
@Executable(name = "return", message = "Returns from current execution. Currently this is supported only in step-group.")
public class ReturnStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		exeLogger.debug("Returning from current execution");
		throw new ReturnException();
	}
}
