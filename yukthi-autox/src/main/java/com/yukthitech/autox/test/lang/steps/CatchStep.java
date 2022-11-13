package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractContainerStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IExecutionLogger;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;

/**
 * Represents catch block.
 * 
 * @author akiran
 */
@Executable(name = "catch", group = Group.Lang, partOf = TryStep.class, message = "Represents steps to be executed on error. This step has to be preceeded by try-step.")
public class CatchStep extends AbstractContainerStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Attribute name for error. Default: error.
	 */
	@Param(description = "Attribute name for error. Default: error", attrName = true)
	private String errorAttr = "error";
	
	public void setErrorAttr(String errorAttr)
	{
		this.errorAttr = errorAttr;
	}
	
	public String getErrorAttr()
	{
		return errorAttr;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		throw new UnsupportedOperationException("Catch cannot be used without try block.");
	}
}
