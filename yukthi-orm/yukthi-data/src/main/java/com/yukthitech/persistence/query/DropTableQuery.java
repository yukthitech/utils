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
package com.yukthitech.persistence.query;

import javax.persistence.GenerationType;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;

/**
 * Drop query to drop underlying entity table 
 * @author akiran
 */
public class DropTableQuery extends Query
{
	private String tableName;
	
	public DropTableQuery(EntityDetails entityDetails)
	{
		super(entityDetails);
		
		this.tableName = entityDetails.getTableName();
	}
	
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * Checks if id field of current table is having sequence based id field.
	 *
	 * @return true, if is sequence id field
	 */
	public boolean isSequenceIdField()
	{
		FieldDetails idField = entityDetails.getIdField();
		
		if(idField == null)
		{
			return false;
		}
		
		return (idField.getGenerationType() == GenerationType.SEQUENCE);  
	}

	/**
	 * Checks if id field of current table is having auto id field.
	 *
	 * @return true, if is auto id field
	 */
	public boolean isAutoIdField()
	{
		FieldDetails idField = entityDetails.getIdField();
		
		if(idField == null)
		{
			return false;
		}
		
		return (idField.getGenerationType() == GenerationType.IDENTITY);  
	}

	/**
	 * Returns the sequence name used by id field of this table.
	 * @return
	 */
	public String getIdSequence()
	{
		FieldDetails idField = entityDetails.getIdField();
		
		if(idField == null)
		{
			return null;
		}
		
		String seqName = idField.getSequenceName();
		seqName = StringUtils.isBlank(seqName)? null : seqName.trim();
		
		return seqName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(getClass().getName());
		builder.append("{ ").append(getTableName()).append(" }");
		return builder.toString();
	}
}
