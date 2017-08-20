package com.yukthitech.autox;

/**
 * Container of steps.
 * @author akiran
 */
public interface IStepContainer
{
	/**
	 * Adds step to this orchestrator.
	 * @param step Step to add.
	 */
	public void addStep(IStep step);
}
