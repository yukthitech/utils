package com.yukthitech.autox.test;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Reference step to execute target step group with specified parameters.
 * @author akiran
 */
@Executable(name = "stepGroupRef", message = "Sets the specified context attribute with specified value")
public class StepGroupRef extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the step group to execute.
	 */
	@Param(description = "Name of the group to execute.")
	private String name;
	
	/**
	 * Parameters for the step group to be executed.
	 */
	@SkipParsing
	@Param(description = "Parameters to be passed to step group.", required = false)
	private Map<String, StepGroupParam> params;
	
	/**
	 * Sets the name of the step group to execute.
	 *
	 * @param name the new name of the step group to execute
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Adds the specified param.
	 * @param param
	 */
	public void addParameter(StepGroupParam param)
	{
		if(this.params == null)
		{
			this.params = new HashMap<>();
		}
		
		this.params.put(param.getName(), param);
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		StepGroup stepGroup = context.getStepGroup(name);
		
		if(stepGroup == null)
		{
			throw new InvalidStateException("No step group found with specified name: {}", name);
		}

		Map<String, StepGroupParam> params = null;
		Map<String, Object> paramValues = null;

		//build the params for step group to execute
		if(this.params != null)
		{
			params = new HashMap<>(this.params);
			params = AutomationUtils.replaceExpressions("params", context, params);

			paramValues = new HashMap<>();
			
			for(String key : params.keySet())
			{
				paramValues.put(key, params.get(key).getResultValue());
			}
		}
		
		stepGroup = (StepGroup) stepGroup.clone();
		stepGroup.setLoggingDisabled(super.isLoggingDisabled());
		stepGroup.setParams(paramValues);
		
		stepGroup.execute(context, logger);
		
		return true;
	}
	
	@Override
	public IStep clone()
	{
		return AutomationUtils.deepClone(this);
	}
}
