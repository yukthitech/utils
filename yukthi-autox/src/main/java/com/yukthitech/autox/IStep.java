package com.yukthitech.autox;

import java.io.Serializable;

/**
 * Represents automation step to be performed.
 * @author akiran
 */
public interface IStep extends Cloneable, Serializable, ILocationBased
{
	/**
	 * Method which should execute current step.
	 * @param context Current automation context
	 * @param logger Logger to log messages.
	 * value will be checked for success.
	 */
	public void execute(AutomationContext context, ExecutionLogger logger) throws Exception;
	
	/**
	 * Clones and makes a copy of current step.
	 * @return copy of current step
	 */
	public IStep clone();
	
	/**
	 * Fetches flag indicating if logging has to be disabled for current step.
	 * @return flag indicating if logging has to be disabled for current step.
	 */
	public boolean isLoggingDisabled();
	
	/**
	 * Sets the source step with actual expressions. Which in turn can be used, when access to actual
	 * expressions is required. This is mainly needed by assertions to build error messages in case of
	 * errors.
	 * @param sourceStep
	 */
	public void setSourceStep(IStep sourceStep);
	
	public IStep getSourceStep();
}
