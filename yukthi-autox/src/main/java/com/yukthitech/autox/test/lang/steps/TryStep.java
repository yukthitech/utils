package com.yukthitech.autox.test.lang.steps;

import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.AbstractContainerStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IMultiPartStep;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.HandledException;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Used to enclose steps to catch errors.
 * 
 * @author akiran
 */
@Executable(name = "try", group = Group.Lang, message = "Used to enclose steps to catch errors.")
public class TryStep extends AbstractContainerStep implements IStepContainer, IMultiPartStep
{
	private static final long serialVersionUID = 1L;
	
	private CatchStep catchStep;
	
	@Override
	public void addChildStep(IStep step)
	{
		if(catchStep != null)
		{
			throw new InvalidArgumentException("Multiple catch steps specified for same try-block");
		}
		
		this.catchStep = (CatchStep) step;
	}

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		try
		{
			StepsExecutor.execute(steps, null);
		}catch(Exception ex)
		{
			if(catchStep == null)
			{
				throw ex;
			}

			if(ex instanceof LangException)
			{
				throw ex;
			}
			
			if(ex instanceof HandledException)
			{
				ex = (Exception) ex.getCause();
			}

			exeLogger.warn("Exception occurred while executing try-block. Executing catch block. Exception: {}", ex);
			
			context.setAttribute(catchStep.getErrorAttr(), ex);
			StepsExecutor.execute(catchStep.getSteps(), null);
		}
	}
}
