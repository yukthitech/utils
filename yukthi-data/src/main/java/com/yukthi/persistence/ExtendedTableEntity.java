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

package com.yukthi.persistence;

/**
 * Dummy entity to represent extendable table entity.
 * @author akiran
 */
public class ExtendedTableEntity
{
	/**
	 * Entity id column name.
	 */
	public static final String COLUMN_ENTITY_ID = "ENTITY_ID";
	
	public static final String FIELD_ENTITY_ID = "entityId";
	
	/**
	 * Field to represent entity id
	 */
	private Long entityId;
	
	/**
	 * Dummy field.
	 */
	private Object dummyField;

	/**
	 * Gets the dummy field.
	 *
	 * @return the dummy field
	 */
	public Object getDummyField()
	{
		return dummyField;
	}

	/**
	 * Sets the dummy field.
	 *
	 * @param dummyField the new dummy field
	 */
	public void setDummyField(Object dummyField)
	{
		this.dummyField = dummyField;
	}

	/**
	 * Gets the field to represent entity id.
	 *
	 * @return the field to represent entity id
	 */
	public Long getEntityId()
	{
		return entityId;
	}

	/**
	 * Sets the field to represent entity id.
	 *
	 * @param entityId the new field to represent entity id
	 */
	public void setEntityId(Long entityId)
	{
		this.entityId = entityId;
	}
}
