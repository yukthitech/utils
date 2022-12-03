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
 * Represents literal value in expression.
 * @author akiran
 */
public class Literal implements IExpressionPart
{
	/**
	 * Literal value.
	 */
	private Object value;

	/**
	 * Instantiates a new literal.
	 *
	 * @param value the value
	 */
	public Literal(Object value)
	{
		this.value = value;
	}
	
	/**
	 * Gets the literal value.
	 *
	 * @return the literal value
	 */
	public Object getValue()
	{
		return value;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IExpressionPart#collectVariables(java.util.Set)
	 */
	@Override
	public void collectVariables(Set<String> variables)
	{
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IExpressionPart#getType(com.yukthitech.webutils.utils.expr.IVariableTypeProvider, com.yukthitech.webutils.utils.expr.ExpressionRegistry)
	 */
	@Override
	public Class<?> getType(IVariableTypeProvider variableTypeProvider, ExpressionRegistry registry)
	{
		return value.getClass();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IExpressionPart#evaluate(com.yukthitech.webutils.utils.expr.IVariableValueProvider, com.yukthitech.webutils.utils.expr.ExpressionRegistry)
	 */
	@Override
	public Object evaluate(IVariableValueProvider variableValueProvider, ExpressionRegistry registry)
	{
		return value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "" + value;
	}
}
