package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yukthitech.autox.AbstractLocationBased;
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
@Executable(name = "stepGroup", message = "Can be used to group multiple steps into single step")
public class StepGroup extends AbstractLocationBased implements IStepContainer, IStep, Cloneable
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
	 * Flag indicating if logging is disabled. This flag is expected to be set
	 * by calling step-group-ref.
	 */
	private boolean loggingDisabled = false;
	
	/**
	 * Params for step group execution. This are expected to be set by step-group-ref
	 */
	private Map<String, Object> params;
	
	/**
	 * Optional data provider for the step.
	 */
	@SkipParsing
	private IDataProvider dataProvider;

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
	
	/**
	 * Sets the flag indicating if logging is disabled. This flag is expected to be set by calling step-group-ref.
	 *
	 * @param loggingDisabled the new flag indicating if logging is disabled
	 */
	void setLoggingDisabled(boolean loggingDisabled)
	{
		this.loggingDisabled = loggingDisabled;
	}
	
	void setParams(Map<String, Object> params)
	{
		this.params = params;
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
			/*
			 * parameters needs to be set in loop so that in case of recursion or one step group
			 * calling other, current group parameters will be restored before executing next step.
			 */
			context.setAttribute("parameters", params);
			
			try
			{
				StepExecutor.executeStep(context, logger, step);
			} catch (Exception ex)
			{
				if(functionGroup && (ex instanceof ReturnException))
				{
					logger.debug(this, "Exiting from current step group");
					break;
				}
				
				if(ex instanceof LangException)
				{
					throw ex;
				}
				
				Executable executable = step.getClass().getAnnotation(Executable.class);
				logger.error(this, "An error occurred while executing child-step '{}'. Error: {}", executable.name()[0], ex);
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
		return loggingDisabled;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#getDataProvider()
	 */
	public IDataProvider getDataProvider()
	{
		return dataProvider;
	}

	/**
	 * Sets the optional data provider for the step.
	 *
	 * @param dataProvider the new optional data provider for the step
	 */
	public void setDataProvider(IDataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
	}

	/**
	 * Sets the specified list data provider as data-provider for this test case.
	 * @param dataProvider data provider to set
	 */
	public void setListDataProvider(ListDataProvider dataProvider)
	{
		this.setDataProvider(dataProvider);
	}
	
	/**
	 * Sets the specified range data provider as data-provider for this test case.
	 * @param dataProvider data provider to set
	 */
	public void setRangeDataProvider(RangeDataProvider dataProvider)
	{
		this.setDataProvider(dataProvider);
	}
}
