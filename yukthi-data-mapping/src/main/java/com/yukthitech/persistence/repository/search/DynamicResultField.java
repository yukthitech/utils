/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

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

package com.yukthitech.persistence.repository.search;

/**
 * Dynamic field with value from search results
 * @author akiran
 */
public class DynamicResultField
{
	/**
	 * Entity field represented by this field.
	 */
	private String field;
	
	/**
	 * Value of the field
	 */
	private Object value;
	
	/**
	 * Instantiates a new dynamic result field.
	 */
	public DynamicResultField()
	{}
	
	/**
	 * Instantiates a new dynamic result field.
	 *
	 * @param field the field
	 * @param value the value
	 */
	public DynamicResultField(String field, Object value)
	{
		this.field = field;
		this.value = value;
	}

	/**
	 * Gets the entity field represented by this field.
	 *
	 * @return the entity field represented by this field
	 */
	public String getField()
	{
		return field;
	}

	/**
	 * Sets the entity field represented by this field.
	 *
	 * @param field the new entity field represented by this field
	 */
	public void setField(String field)
	{
		this.field = field;
	}

	/**
	 * Gets the value of the field.
	 *
	 * @return the value of the field
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the field.
	 *
	 * @param value the new value of the field
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
}
