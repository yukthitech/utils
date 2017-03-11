package com.yukthitech.automation;

import java.io.Serializable;

/**
 * Represents automation step to be performed.
 * @author akiran
 */
public interface IStep extends Cloneable, Serializable
{
	/**
	 * Method which should execute current step.
	 * @param context Current automation context
	 * @param logger Logger to log messages.
	 */
	public void execute(AutomationContext context, ExecutionLogger logger) throws Exception;
	
	/**
	 * Clones and makes a copy of current step.
	 * @return copy of current step
	 */
	public IStep clone();
}
