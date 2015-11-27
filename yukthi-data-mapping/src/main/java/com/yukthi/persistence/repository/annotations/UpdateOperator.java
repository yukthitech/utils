/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.persistence.repository.annotations;

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
