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

/**
 * Represents variable in expression.
 * @author akiran
 */
public class Variable implements IExpressionPart
{
	/**
	 * Name of the variable.
	 */
	private String name;

	/**
	 * Instantiates a new variable.
	 *
	 * @param name the name
	 */
	public Variable(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the name of the variable.
	 *
	 * @return the name of the variable
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Collect variables.
	 *
	 * @param variables the variables
	 */
	@Override
	public void collectVariables(Set<String> variables)
	{
		variables.add(name);
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
		return variableTypeProvider.getVariableType(name);
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
		return variableValueProvider.getVariableValue(name);
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
		return "@" + name;
	}
}
