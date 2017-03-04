package com.yukthitech.automation;

/**
 * Represents validation to be done.
 * @author akiran
 */
public interface IValidation
{
	/**
	 * Method which should execute current validation.
	 * @param context Current automation context
	 * @param exeLogger Logger to log execution messages.
	 * @return true if validation is successful, otherwise false
	 */
	public boolean validate(AutomationContext context, ExecutionLogger exeLogger);
}
