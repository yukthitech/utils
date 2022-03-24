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
@Executable(name = "throw", group = Group.Lang, message = "Fails the current test case by throwing fail exception.")
public class ThrowStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Message to be used to throw as part of error.
	 */
	@Param(description = "Message to be used to throw as part of error", required = false, sourceType = SourceType.EXPRESSION)
	private String message;
	
	/**
	 * Value to be sent along with error.
	 */
	@Param(description = "Value to be sent along with error.", required = false, sourceType = SourceType.EXPRESSION)
	private Object value;

	/**
	 * Sets the message to be used to fail test case or test suite.
	 *
	 * @param message the new message to be used to fail test case or test suite
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	/**
	 * Sets the value to be sent along with error.
	 *
	 * @param value
	 *            the new value to be sent along with error
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		exeLogger.debug("Throwing error with message: {}", message);
		throw new ValuedException(message, value);
	}
}
