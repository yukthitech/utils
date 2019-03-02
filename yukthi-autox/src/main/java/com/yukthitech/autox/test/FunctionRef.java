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
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Reference step to execute target function with specified parameters.
 * @author akiran
 */
@Executable(name = "functionRef", message = "Reference step to execute target function with specified parameters.")
public class FunctionRef extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the step group to execute.
	 */
	@Param(description = "Name of the function to execute.")
	private String name;
	
	/**
	 * Parameters for the step group to be executed.
	 */
	@Param(description = "Parameters to be passed to function.", required = false)
	private Map<String, FunctionParam> params;
	
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
	public void addParameter(FunctionParam param)
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
		Function function = context.getFunction(name);
		
		if(function == null)
		{
			throw new InvalidStateException("No function found with specified name: {}", name);
		}

		Map<String, Object> paramValues = null;

		//build the params for step group to execute
		if(this.params != null)
		{
			paramValues = new HashMap<>();
			
			for(String key : params.keySet())
			{
				paramValues.put(key, params.get(key).getValue());
			}
		}
		
		function = (Function) function.clone();
		function.setLoggingDisabled(super.isLoggingDisabled());
		
		logger.debug(this, "Executing function '{}' with parameters: {}", name, paramValues);
		
		function.setParams(paramValues);
		function.execute(context, logger);
		
		return true;
	}
	
	@Override
	public IStep clone()
	{
		return AutomationUtils.deepClone(this);
	}
}