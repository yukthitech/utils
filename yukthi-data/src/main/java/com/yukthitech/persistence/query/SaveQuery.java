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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.GenerationType;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;

public class SaveQuery extends Query
{
	private List<ColumnParam> columns;

	public SaveQuery(EntityDetails entityDetails)
	{
		super(entityDetails);
	}

	/** 
	 * Adds value to {@link #columns Columns}
	 *
	 * @param column column to be added
	 */
	public void addColumn(ColumnParam column)
	{
		if(columns == null)
		{
			columns = new ArrayList<ColumnParam>();
		}

		columns.add(column);
	}

	public List<ColumnParam> getColumns()
	{
		return columns;
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
	
	public String getIdFieldName()
	{
		FieldDetails idField = entityDetails.getIdField();
		
		if(idField == null)
		{
			return null;
		}

		return idField.getName();
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
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Columns: ").append(columns);

		builder.append("]");
		return builder.toString();
	}
}
