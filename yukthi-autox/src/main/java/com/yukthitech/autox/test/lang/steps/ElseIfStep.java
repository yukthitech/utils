package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractContainerStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Represents else-if block.
 * 
 * @author akiran
 */
@Executable(name = "elseIf", group = Group.Lang, partOf = TryStep.class, message = "Represents steps to be executed based on condition when prior if condition fails.")
public class ElseIfStep extends AbstractContainerStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Freemarker condition to be evaluated.
	 */
	@Param(description = "Freemarker condition to be evaluated.", required = true, sourceType = SourceType.CONDITION)
	private String condition;

	public void setCondition(String condition)
	{
		this.condition = condition;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		throw new UnsupportedOperationException("else-if cannot be used without if block.");
	}
}
