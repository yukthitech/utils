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
