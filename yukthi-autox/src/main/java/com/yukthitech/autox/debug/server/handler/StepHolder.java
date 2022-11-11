package com.yukthitech.autox.debug.server.handler;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.test.CustomUiLocator;
import com.yukthitech.autox.test.Function;

class StepHolder implements IStepContainer
{
	/**
	 * Steps added for execution.
	 */
	private List<IStep> steps = new ArrayList<>();
	
	/**
	 * Custom ui locators to reload.
	 */
	private List<CustomUiLocator> customUiLocators = new ArrayList<>();
	
	/**
	 * Functions to reload.
	 */
	private List<Function> functions = new ArrayList<>();

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
	
	public void addCustomUiLocator(CustomUiLocator locator)
	{
		this.customUiLocators.add(locator);
	}
	
	public List<CustomUiLocator> getCustomUiLocators()
	{
		return customUiLocators;
	}
	
	public void addFunction(Function function)
	{
		this.functions.add(function);
	}
	
	public List<Function> getFunctions()
	{
		return functions;
	}
}
