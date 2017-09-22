package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.test.lang.steps.LangException;
import com.yukthitech.autox.test.lang.steps.ReturnException;

/**
 * Represents group of steps and/or validations. That can be referenced 
 * @author akiran
 */
public class StepGroup implements IStepContainer, IStep, Cloneable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of this group.
	 */
	private String name;
	
	/**
	 * Steps for the test case.
	 */
	@SkipParsing
	private List<IStep> steps = new ArrayList<>();
	
	/**
	 * Flag indicating if the current group is function group. Return statement will
	 * be executed only in function groups.
	 */
	private boolean functionGroup = false;

	/**
	 * Sets the name of this group.
	 *
	 * @param name the new name of this group
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the name of this group.
	 *
	 * @return the name of this group
	 */
	public String getName()
	{
		return name;
	}
	
	public void markAsFunctionGroup()
	{
		this.functionGroup = true;
	}

	@Override
	public void addStep(IStep step)
	{
		steps.add(step);
	}

	/**
	 * Gets the steps for the test case.
	 *
	 * @return the steps for the test case
	 */
	public List<IStep> getSteps()
	{
		return steps;
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		for(IStep step : this.steps)
		{
			try
			{
				StepExecutor.executeStep(context, logger, step);
			} catch (Exception ex)
			{
				if(functionGroup && (ex instanceof ReturnException))
				{
					logger.debug("Exiting from current step group");
					break;
				}
				
				if(ex instanceof LangException)
				{
					throw ex;
				}
				
				Executable executable = step.getClass().getAnnotation(Executable.class);
				logger.error("An error occurred while executing child-step '{}'. Error: {}", executable.name(), ex);
				throw ex;
			}
		}
		
		return true;
	}

	@Override
	public IStep clone()
	{
		try
		{
			return (IStep) super.clone();
		} catch (CloneNotSupportedException ex)
		{
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public boolean isLoggingDisabled()
	{
		return false;
	}
}
