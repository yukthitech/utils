package com.yukthitech.autox.action;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;

/**
 * Represents a action plan that can be executed with automation engine.
 * @author akiran
 */
public class ActionPlan implements IStepContainer
{
	/**
	 * Steps added for execution.
	 */
	private List<IStep> steps = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IStepContainer#addStep(com.yukthitech.ui.automation.
	 * IStep)
	 */
	@Override
	public void addStep(IStep step)
	{
		steps.add(step);
	}

	/**
	 * Gets the steps added for execution.
	 *
	 * @return the steps added for execution
	 */
	public List<IStep> getSteps()
	{
		return steps;
	}
}
