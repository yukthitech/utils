package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Continues current loop.
 * @author akiran
 */
@Executable(name = "continue", group = Group.Lang, message = "Continues current loop")
public class ContinueStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		exeLogger.debug("Contining current loop");
		throw new ContinueException();
	}
}
