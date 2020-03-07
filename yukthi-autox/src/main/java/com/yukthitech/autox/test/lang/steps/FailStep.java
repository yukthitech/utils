package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Fails the current test case by throwing fail exception.
 * @author akiran
 */
@Executable(name = "fail", group = Group.Lang, message = "Fails the current test case by throwing fail exception.")
public class FailStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Message to be used to fail test case or test suite.
	 */
	@Param(description = "Message ot the fail exception to be thrown", required = false, sourceType = SourceType.EXPRESSION)
	private String message;
	
	/**
	 * Sets the message to be used to fail test case or test suite.
	 *
	 * @param message the new message to be used to fail test case or test suite
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		exeLogger.debug("Failing the current test case with message: {}", message);
		
		if(message == null)
		{
			throw new FailException();
		}
		else
		{
			throw new FailException(message);
		}
	}
}
