/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.utils.expr;

import java.util.Set;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Represents expression part which is a function invocation.
 * @author akiran
 */
public class FunctionExpr implements IExpressionPart
{
	/**
	 * Name of the function.
	 */
	private String name;
	
	/**
	 * Function parameters as expression parts.
	 */
	private IExpressionPart parameters[];

	/**
	 * Instantiates a new function expr.
	 *
	 * @param name the name
	 * @param params the params
	 */
	public FunctionExpr(String name, IExpressionPart... params)
	{
		this.name = name;
		this.parameters = params;
	}
	
	/**
	 * Returns the name of the function.
	 * @return Name of the function.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Return parameters of the function as expression parts.
	 * @return function parameters
	 */
	public IExpressionPart[] getParameters()
	{
		return parameters;
	}
	
	/**
	 * Collect variables.
	 *
	 * @param variables the variables
	 */
	@Override
	public void collectVariables(Set<String> variables)
	{
		if(parameters == null)
		{
			return;
		}
		
		for(IExpressionPart expression : parameters)
		{
			expression.collectVariables(variables);
		}
	}

	/**
	 * Gets the type.
	 *
	 * @param variableTypeProvider the variable type provider
	 * @param registry the registry
	 * @return the type
	 */
	@Override
	public Class<?> getType(IVariableTypeProvider variableTypeProvider, ExpressionRegistry registry)
	{
		Class<?> paramTypes[] = new Class<?>[parameters == null ? 0 : parameters.length];
		
		if(parameters != null)
		{
			int idx = 0;
			
			for(IExpressionPart parameter : parameters)
			{
				paramTypes[idx] = parameter.getType(variableTypeProvider, registry);
				idx++;
			}
		}
		
		IFunction function = registry.getFunction(name);
		
		if(function == null)
		{
			throw new InvalidArgumentException("Invalid function name encountered - {}", name);
		}
		
		return function.getReturnType(paramTypes);
	}

	/**
	 * Evaluate.
	 *
	 * @param variableValueProvider the variable value provider
	 * @param registry the registry
	 * @return the object
	 */
	@Override
	public Object evaluate(IVariableValueProvider variableValueProvider, ExpressionRegistry registry)
	{
		Object paramValues[] = new Object[parameters == null ? 0 : parameters.length];
		
		if(parameters != null)
		{
			int idx = 0;
			
			for(IExpressionPart parameter : parameters)
			{
				paramValues[idx] = parameter.evaluate(variableValueProvider, registry);
				idx++;
			}
		}
		
		IFunction function = registry.getFunction(name);
		
		if(function == null)
		{
			throw new InvalidArgumentException("Invalid function name encountered - {}", name);
		}
		
		return function.evaluate(paramValues);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(name);
		builder.append("(");
		
		if(parameters != null && parameters.length > 0)
		{
			for(IExpressionPart param : parameters)
			{
				builder.append(param).append(", ");
			}
			
			builder.delete(builder.length() - 2, builder.length());
		}
		
		builder.append(")");
		return builder.toString();
	}
}
