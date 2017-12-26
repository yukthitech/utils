package com.yukthitech.autox.ide.engine;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;

public class StepHolder implements IStepContainer
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
