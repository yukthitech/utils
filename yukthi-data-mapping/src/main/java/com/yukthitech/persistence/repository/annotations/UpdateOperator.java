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
package com.yukthitech.persistence.repository.annotations;

/**
 * Used in {@link Field} annotation in update queries, to update entity with value based on current value.
 * By default {@link #NONE} is used, which means specified value will be set as new value on the target entity field.
 * @author akiran
 */
public enum UpdateOperator
{
	/**
	 * Specified value will be set on target entity field.
	 */
	NONE(""), 
	
	/**
	 * Adds to the current value. <BR>
	 * new-value = current-value + specified-value
	 */
	ADD("+"),

	/**
	 * Subtracts from current value. <BR>
	 * new-value = current-value - specified-value
	 */
	SUBTRACT("-"),
	
	/**
	 * Multiplies to current value. <BR>
	 * new-value = current-value * specified-value
	 */
	MULTIPLY("*"),
	
	/**
	 * Divides current value with specified value. <BR>
	 * new-value = current-value / specified-value
	 */
	DIVIDE("/")
	;
	
	/**
	 * Arithmetic operator to be used
	 */
	private String op;
	
	/**
	 * Instantiates a new update operator.
	 *
	 * @param op the op
	 */
	private UpdateOperator(String op)
	{
		this.op = op;
	}

	/**
	 * Checks if current operator is none operator
	 * @return True if current operator is NONE
	 */
	public boolean isNone()
	{
		return (this == UpdateOperator.NONE);
	}
	
	/**
	 * Gets the arithmetic operator to be used.
	 *
	 * @return the arithmetic operator to be used
	 */
	public String getOp()
	{
		return op;
	}
}
