package com.yukthitech.autox;

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
	 * @return In case step, the return value will not be considered. In case of validation, the return 
	 * value will be checked for success.
	 */
	public boolean execute(AutomationContext context, ExecutionLogger logger) throws Exception;
	
	/**
	 * Clones and makes a copy of current step.
	 * @return copy of current step
	 */
	public IStep clone();
}
