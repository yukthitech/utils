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
 * Thrown when unique constraint on entity is violated
 * @author akiran
 */
public class UniqueConstraintViolationException extends PersistenceException
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Entity type on which this constraint is defined
	 */
	private Class<?> entityType;
	
	/**
	 * Fields which are involved in unique constraint
	 */
	private String fields[];
	
	/**
	 * Name of the unique constraint name
	 */
	private String constraintName;

	/**
	 * Instantiates a new unique constraint violation exception.
	 *
	 * @param entityType the entity type
	 * @param fields the fields
	 * @param constraintName the constraint name
	 * @param message the message
	 * @param cause the cause
	 */
	public UniqueConstraintViolationException(Class<?> entityType, String fields[], String constraintName, String message, Throwable cause)
	{
		super(message, cause);
		
		this.entityType = entityType;
		this.fields = fields;
		this.constraintName = constraintName;
	}

	/**
	 * Instantiates a new unique constraint violation exception.
	 *
	 * @param entityType the entity type
	 * @param fields the fields
	 * @param constraintName the constraint name
	 * @param message the message
	 */
	public UniqueConstraintViolationException(Class<?> entityType, String fields[], String constraintName, String message)
	{
		this(entityType, fields, constraintName, message, null);
	}

	/**
	 * Gets the entity type on which this constraint is defined.
	 *
	 * @return the entity type on which this constraint is defined
	 */
	public Class<?> getEntityType()
	{
		return entityType;
	}

	/**
	 * Gets the fields which are involved in unique constraint.
	 *
	 * @return the fields which are involved in unique constraint
	 */
	public String[] getFields()
	{
		return fields;
	}

	/**
	 * Gets the name of the unique constraint name.
	 *
	 * @return the name of the unique constraint name
	 */
	public String getConstraintName()
	{
		return constraintName;
	}
}
