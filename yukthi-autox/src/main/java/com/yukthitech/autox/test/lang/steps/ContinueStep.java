package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;

/**
 * Continues current loop.
 * @author akiran
 */
@Executable(name = "continue", message = "Continues current loop")
public class ContinueStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		exeLogger.debug("Contining current loop");
		throw new ContinueException();
	}
}
