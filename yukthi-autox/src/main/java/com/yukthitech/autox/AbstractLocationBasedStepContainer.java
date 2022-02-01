package com.yukthitech.autox;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.common.SkipParsing;

public abstract class AbstractLocationBasedStepContainer extends AbstractLocationBased implements IStepContainer
{
	/**
	 * Steps for the test case.
	 */
	@SkipParsing
	protected List<IStep> steps = new ArrayList<>();

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
		if(steps == null)
		{
			steps = new ArrayList<IStep>();
		}

		steps.add(step);
	}
	
	@Override
	public List<IStep> getSteps()
	{
		return steps;
	}
}
