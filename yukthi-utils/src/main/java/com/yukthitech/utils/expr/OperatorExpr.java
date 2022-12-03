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
 * Operator based expression part with operands.
 * @author akiran
 */
public class OperatorExpr implements IExpressionPart
{
	/**
	 * Left operand.
	 */
	private IExpressionPart left;
	
	/**
	 * Operator to be used.
	 */
	private String operator;
	
	/**
	 * Right operand.
	 */
	private IExpressionPart right;

	/**
	 * Instantiates a new operator expr.
	 *
	 * @param left the left
	 * @param operator the operator
	 * @param right the right
	 */
	public OperatorExpr(IExpressionPart left, String operator, IExpressionPart right)
	{
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	/**
	 * Gets the left operand.
	 *
	 * @return the left operand
	 */
	public IExpressionPart getLeft()
	{
		return left;
	}

	/**
	 * Gets the operator to be used.
	 *
	 * @return the operator to be used
	 */
	public String getOperator()
	{
		return operator;
	}

	/**
	 * Gets the right operand.
	 *
	 * @return the right operand
	 */
	public IExpressionPart getRight()
	{
		return right;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IExpressionPart#collectVariables(java.util.Set)
	 */
	@Override
	public void collectVariables(Set<String> variables)
	{
		left.collectVariables(variables);
		right.collectVariables(variables);
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IExpressionPart#getType(com.yukthitech.webutils.utils.expr.IVariableTypeProvider, com.yukthitech.webutils.utils.expr.ExpressionRegistry)
	 */
	@Override
	public Class<?> getType(IVariableTypeProvider variableTypeProvider, ExpressionRegistry registry)
	{
		IOperator operatorObj = registry.getOperator(operator);
		
		if(operatorObj == null)
		{
			throw new InvalidArgumentException("Invalid operator encountered - {}", operator);
		}
		
		return operatorObj.getReturnType(left.getType(variableTypeProvider, registry), right.getType(variableTypeProvider, registry));
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IExpressionPart#evaluate(com.yukthitech.webutils.utils.expr.IVariableValueProvider, com.yukthitech.webutils.utils.expr.ExpressionRegistry)
	 */
	@Override
	public Object evaluate(IVariableValueProvider variableValueProvider, ExpressionRegistry registry)
	{
		IOperator operatorObj = registry.getOperator(operator);
		
		if(operatorObj == null)
		{
			throw new InvalidArgumentException("Invalid operator encountered - {}", operator);
		}
		
		return operatorObj.evaluate(left.evaluate(variableValueProvider, registry), right.evaluate(variableValueProvider, registry));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(left);
		builder.append(" ").append(operator).append(" ");
		builder.append(right);
		builder.append("]");
		return builder.toString();
	}
}
