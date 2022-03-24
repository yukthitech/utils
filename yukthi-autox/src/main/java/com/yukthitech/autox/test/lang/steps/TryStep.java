package com.yukthitech.autox.test.lang.steps;

import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.AbstractContainerStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IMultiPartStep;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;

/**
 * Used to enclose steps to catch errors.
 * 
 * @author akiran
 */
@Executable(name = "try", group = Group.Lang, message = "Used to enclose steps to catch errors.")
public class TryStep extends AbstractContainerStep implements IStepContainer, IMultiPartStep
{
	private static final long serialVersionUID = 1L;
	
	private TryCatchStep catchSteps;
	
	@Override
	public void addChildStep(IStep step)
	{
		if(catchSteps != null)
		{
			throw new InvalidArgumentException("Multiple catch steps specified for same try-block");
		}
		
		this.catchSteps = (TryCatchStep) step;
	}

	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		try
		{
			steps.execute(context, exeLogger, true);
		}catch(Exception ex)
		{
			if(catchSteps == null)
			{
				throw ex;
			}

			if(ex instanceof LangException)
			{
				throw (LangException) ex;
			}
			
			context.setAttribute(catchSteps.getErrorAttr(), ex);
			catchSteps.execute(context, exeLogger);
		}
	}
}
