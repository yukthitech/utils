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

/**
 * Represents evaluatable operator.
 * @author akiran
 */
public interface IOperator
{
	/**
	 * Gets operator as string.
	 * @return operator string.
	 */
	public String getOperator();
	
	/**
	 * Fetches description of the operator.
	 * @return Description
	 */
	public String getDescription();
	
	/**
	 * Return type of the operator for specified operand types.
	 * @param leftOperand left operand type
	 * @param rightOperand right operand type
	 * @return Return type
	 */
	public Class<?> getReturnType(Class<?> leftOperand, Class<?> rightOperand);
	
	/**
	 * Evaluates the operator with specified operand values.
	 * @param leftOperand Left operand value
	 * @param rightOperand right operand value
	 * @return Result of operator
	 */
	public Object evaluate(Object leftOperand, Object rightOperand);
}
