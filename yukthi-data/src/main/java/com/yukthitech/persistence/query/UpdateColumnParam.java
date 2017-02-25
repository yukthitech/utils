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

package com.yukthitech.persistence.query;

import com.yukthitech.persistence.repository.annotations.UpdateOperator;

/**
 * Column parameter details for update query
 * @author akiran
 */
public class UpdateColumnParam extends ColumnParam
{
	/**
	 * Update operator to be used
	 */
	private UpdateOperator updateOperator;
	
	/**
	 * Instantiates a new update column param.
	 *
	 * @param name the name
	 * @param value the value
	 * @param index the index
	 * @param sequence the sequence
	 * @param updateOp the update op
	 */
	public UpdateColumnParam(String name, Object value, int index, String sequence, UpdateOperator updateOp)
	{
		super(name, value, index, sequence);
		this.updateOperator = updateOp;
	}

	/**
	 * Instantiates a new update column param.
	 *
	 * @param name the name
	 * @param value the value
	 * @param index the index
	 * @param updateOp the update op
	 */
	public UpdateColumnParam(String name, Object value, int index, UpdateOperator updateOp)
	{
		super(name, value, index);
		this.updateOperator = updateOp;
	}

	/**
	 * Gets the update operator to be used.
	 *
	 * @return the update operator to be used
	 */
	public UpdateOperator getUpdateOperator()
	{
		return updateOperator;
	}

	/**
	 * Sets the update operator to be used.
	 *
	 * @param updateOperator the new update operator to be used
	 */
	public void setUpdateOperator(UpdateOperator updateOperator)
	{
		this.updateOperator = updateOperator;
	}
}
