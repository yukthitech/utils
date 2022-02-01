package com.yukthitech.autox;

import java.util.List;

import com.yukthitech.autox.test.Function;

public abstract class AbstractContainerStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Steps for the test case.
	 */
	protected Function steps;

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
			steps = new Function();
		}

		steps.addStep(step);
	}
	
	@Override
	public List<IStep> getSteps()
	{
		return steps.getSteps();
	}
}
