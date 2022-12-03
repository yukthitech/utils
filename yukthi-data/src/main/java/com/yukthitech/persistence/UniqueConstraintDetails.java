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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UniqueConstraintDetails
{
	public static final String UNIQUE_CONSTRAINT_PREFIX = "UQ_";
	
	private EntityDetails entityDetails;
	private String name;
	private List<String> fields = new ArrayList<>();
	private String message;
	private boolean validate;
	
	/**
	 * Indicates that specified constraint name is final name. And framework 
	 * should not prefix or suffix with anything else.
	 */
	private boolean finalName;

	public UniqueConstraintDetails(EntityDetails entityDetails, String name, String fields[], String message, boolean validate, boolean finalName)
	{
		if(name == null || name.trim().length() == 0)
		{
			throw new NullPointerException("Name can not be null or empty");
		}
		
		if(fields == null || fields.length == 0)
		{
			throw new NullPointerException("Fields can not be null or empty");
		}

		this.entityDetails = entityDetails;
		this.name = name;
		this.message = (message == null || message.trim().length() == 0) ? null : message.trim();
		this.validate = validate;
		this.finalName = finalName;
		
		this.fields.addAll(Arrays.asList(fields));
	}
	
	/**
	 * @return the {@link #entityDetails entityDetails}
	 */
	public EntityDetails getEntityDetails()
	{
		return entityDetails;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getConstraintName()
	{
		if(finalName)
		{
			return name;
		}
		
		return  UNIQUE_CONSTRAINT_PREFIX + entityDetails.getEntityType().getSimpleName().toUpperCase() + "_" + name.toUpperCase();
	}
	
	public String getFieldsString()
	{
		return fields.toString();
	}

	public List<String> getFields()
	{
		return Collections.unmodifiableList(fields);
	}
	
	public boolean hasField(String field)
	{
		return fields.contains(field);
	}

	public String getMessage()
	{
		return message;
	}

	void setMessage(String message)
	{
		this.message = message;
	}

	public boolean isValidate()
	{
		return validate;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Name: ").append(name);
		builder.append(",").append("Fields: ").append(fields);

		builder.append("]");
		return builder.toString();
	}
}
