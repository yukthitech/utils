package com.yukthitech.automation;

/**
 * Represents automation step to be performed.
 * @author akiran
 */
public interface IStep
{
	/**
	 * Method which should execute current step.
	 * @param context Current automation context
	 * @param logger Logger to log messages.
	 */
	public void execute(AutomationContext context, IExecutionLogger logger);
}
