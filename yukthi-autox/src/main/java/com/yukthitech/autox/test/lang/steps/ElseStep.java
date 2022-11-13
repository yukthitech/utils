package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractContainerStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IExecutionLogger;
import com.yukthitech.autox.IStepContainer;

/**
 * Represents else block.
 * 
 * @author akiran
 */
@Executable(name = "else", group = Group.Lang, partOf = IfConditionStep.class, message = "Represents steps to be executed as part of else block.")
public class ElseStep extends AbstractContainerStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		throw new UnsupportedOperationException("Else cannot be used without if block.");
	}
}
