package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;

/**
 * Fails the current test case by throwing fail exception.
 * @author akiran
 */
@Executable(name = "fail", message = "Fails the current test case by throwing fail exception.")
public class FailStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		exeLogger.debug("Failing the current test case.");
		throw new FailException();
	}
}
