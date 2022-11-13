package com.yukthitech.autox;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.common.SkipParsing;

public abstract class AbstractContainerStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Steps for the test case.
	 */
	@SkipParsing
	protected List<IStep> steps = new ArrayList<IStep>();

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
	
	@Override
	public List<IStep> getSteps()
	{
		return steps;
	}
	
	public void setSteps(List<IStep> steps)
	{
		this.steps = steps;
	}
}
