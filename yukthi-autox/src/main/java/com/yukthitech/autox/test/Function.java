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
import com.yukthitech.ccg.xml.IParentAware;

/**
 * Represents group of steps and/or validations. That can be referenced 
 * @author akiran
 */
@Executable(name = "function", message = "Can be used to group multiple steps into single step")
public class Function extends AbstractLocationBased implements IStepContainer, IStep, Cloneable, IEntryPoint, IParentAware
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

	@SkipParsing
	private Object parent;
	
	@Override
	public void setParent(Object parent)
	{
		this.parent = parent;
	}

	@Override
	public String toText()
	{
		return parent + "." + name;
	}
	
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
	 * Sets the parent where this function is defined.
	 *
	 * @param parent the new parent where this function is defined
	 */
	public void setParent(String parent)
	{
		this.parent = parent;
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
	
	public boolean execute(AutomationContext context, ExecutionLogger logger, boolean inlineExecution) throws Exception
	{
		/*
		 * Note: function invocation may happen as part of other lang steps like, if, for, while etc.
		 * In such cases, the current params should be retained and new params should not be pushed.
		 */
		
		if(!inlineExecution)
		{
			context.pushParameters(params);
			context.getExecutionStack().push(this);
		}

		try
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
						logger.debug("Exiting from current function invocation");
						break;
					}
					
					if(ex instanceof LangException)
					{
						throw ex;
					}
					
					Executable executable = step.getClass().getAnnotation(Executable.class);
					logger.error("An error occurred while executing child-step '{}'. Error: {}", executable.name()[0], ex);
					throw ex;
				}
			}
		}finally
		{
			if(!inlineExecution)
			{
				context.getExecutionStack().pop(this);
				context.popParameters();
			}
		}
		
		return true;
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		return execute(context, logger, false);
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
