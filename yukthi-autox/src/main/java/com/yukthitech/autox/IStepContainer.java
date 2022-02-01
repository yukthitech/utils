package com.yukthitech.autox;

import java.util.List;

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
	
	public List<IStep> getSteps();
}
