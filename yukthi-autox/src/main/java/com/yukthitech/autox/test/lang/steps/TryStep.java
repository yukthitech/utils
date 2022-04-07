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
import com.yukthitech.autox.exec.AutomationExecutor;

/**
 * Used to enclose steps to catch errors.
 * 
 * @author akiran
 */
@Executable(name = "try", group = Group.Lang, message = "Used to enclose steps to catch errors.")
public class TryStep extends AbstractContainerStep implements IStepContainer, IMultiPartStep
{
	private static final long serialVersionUID = 1L;
	
	private TryCatchStep catchStep;
	
	@Override
	public void addChildStep(IStep step)
	{
		if(catchStep != null)
		{
			throw new InvalidArgumentException("Multiple catch steps specified for same try-block");
		}
		
		this.catchStep = (TryCatchStep) step;
	}

	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		AutomationExecutor executor = context.getAutomationExecutor();
		
		executor.newSteps("try-steps", this, super.steps)
			.exceptionHandler((entry, ex) -> 
			{
				if(catchStep == null)
				{
					return false;
				}

				if(ex instanceof LangException)
				{
					return false;
				}
				
				exeLogger.warn("Exception occurred while executing try-block. Executing catch block. Exception: {}", ex);
				
				entry.skipChildSteps();
				context.setAttribute(catchStep.getErrorAttr(), ex);
				catchStep.execute(context, exeLogger);
			
				return true;
			})
			.execute();
	}
}
