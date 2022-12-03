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
package com.yukthitech.persistence;

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
