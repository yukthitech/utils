package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.test.lang.steps.ReturnException;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents group of steps and/or validations. That can be referenced 
 * @author akiran
 */
public class StepGroup implements IStepContainer, Validateable, IStep, Cloneable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of this group.
	 */
	private String name;
	
	/**
	 * Steps for the test case.
	 */
	private List<IStep> steps = new ArrayList<>();

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
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("No/empty name specified.");
		}
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		for(IStep step : this.steps)
		{
			try
			{
				step.execute(context, logger);
			} catch(ReturnException ex)
			{
				break;
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
