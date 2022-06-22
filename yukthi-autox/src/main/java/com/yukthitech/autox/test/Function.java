/*
 * 
 */
package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.yukthitech.autox.AbstractLocationBased;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.test.lang.steps.ReturnException;
import com.yukthitech.ccg.xml.IParentAware;

/**
 * Represents group of steps and/or validations. That can be referenced 
 * @author akiran
 */
public class Function extends AbstractLocationBased implements IStepContainer, Cloneable, IEntryPoint, IParentAware
{
	private static final String VAR_RET_VAL = "returnVal";
	
	/**
	 * Name of this group.
	 */
	@Param(name = "name", description = "Name of the function", required = true)
	private String name;
	
	/**
	 * Description of the function.
	 */
	@SkipParsing
	@Param(name = "description", description = "Description of the function", required = false)
	private String description;
	
	/**
	 * Description of the return value. This should be omitted for functions which dont return any value.
	 */
	@SkipParsing
	@Param(name = "returnDescription", 
		description = "Return description of the function. If not specified, function is assumed will not return any value", 
		required = false)
	private String returnDescription;
	
	/**
	 * Parameter definitions of the function.
	 */
	@SkipParsing
	private List<FunctionParamDef> parameterDefs;
	
	/**
	 * Steps for the test case.
	 */
	@SkipParsing
	private List<IStep> steps = new ArrayList<>();
	
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
	
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of the function.
	 *
	 * @param description the new description of the function
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Adds the specified parameter def to this function.
	 * @param def def to add.
	 */
	public void addParamDef(FunctionParamDef def)
	{
		if(this.parameterDefs == null)
		{
			this.parameterDefs = new ArrayList<>();
		}
		
		this.parameterDefs.add(def);
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
	
	void setParams(Map<String, Object> params)
	{
		this.params = params;
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
	
	public void execute(AutomationContext context, ExecutionLogger logger, Consumer<Object> callback)
	{
		context.getAutomationExecutor()
			.newSteps("function-" + name + "-steps", this, steps)
			.onInit(entry -> 
			{
				context.pushParameters(params);
				context.getExecutionStack().push(this);
				return true;
			})
			.exceptionHandler((entry, ex) -> 
			{
				//occurs during return statement execution
				//Note: even for return false is returned, to ensure current function is removed
				//  from current execution stack and further execution does not occur.
				if(ex instanceof ReturnException)
				{
					entry.setVariable(VAR_RET_VAL, ((ReturnException) ex).getValue() );
					return true;
				}
				
				return false;
			})
			.onSuccess(entry -> 
			{
				callback.accept(entry.getVariable(VAR_RET_VAL));
			})
			.onComplete(entry -> 
			{
				context.getExecutionStack().pop(this);
				context.popParameters();
			})
			.execute();
			;
	}
	
	@Override
	public Function clone()
	{
		try
		{
			return (Function) super.clone();
		} catch (CloneNotSupportedException ex)
		{
			throw new IllegalStateException(ex);
		}
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
