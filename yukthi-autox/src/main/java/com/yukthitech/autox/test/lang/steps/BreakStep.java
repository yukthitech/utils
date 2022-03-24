package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;

/**
 * Breaks current loop.
 * @author akiran
 */
@Executable(name = "break", group = Group.Lang, message = "Breaks current loop")
public class BreakStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		exeLogger.debug("Breaking current loop");
		throw new BreakException();
	}
}
