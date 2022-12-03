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
package com.yukthitech.persistence.query.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.ForeignConstraintDetails;
import com.yukthitech.persistence.UniqueConstraintDetails;

public class TableStructure
{
	private String tableName;
	private List<ColumnStructure> columns = new ArrayList<>();
	private List<UniqueConstraintStructure> uniqueConstraints = new ArrayList<>();
	private List<ForeignConstraintStructure> foreignConstraints = new ArrayList<>();
	
	/**
	 * Field name to column name mapping
	 */
	private Map<String, String> fieldMapping = new HashMap<>();
	
	private ColumnStructure idColumn;
	
	public TableStructure(EntityDetails entityDetails)
	{
		Class<?> entityType = entityDetails.getEntityType();
		tableName = entityDetails.getTableName();

		//Load columns for the table
		ColumnStructure column = null;
		
		if(entityDetails.hasIdField())
		{
			column = new ColumnStructure(entityType, entityDetails.getIdField());
			
			columns.add(column);
			fieldMapping.put(entityDetails.getIdField().getName(), column.getName());
			
			idColumn = column;
		}		
		
		for(FieldDetails field: entityDetails.getFieldDetails())
		{
			if(fieldMapping.containsKey(field.getName()))
			{
				continue;
			}
			
			//if the field is relation field, but not maintained within the current table
			if(field.isMappedRelationField() || field.isTableJoined())
			{
				continue;
			}
			
			column = new ColumnStructure(entityType, field);
			
			columns.add(column);
			fieldMapping.put(field.getName(), column.getName());
		}
		
		//Load unique constraints of the table
		for(UniqueConstraintDetails constraint: entityDetails.getUniqueConstraints())
		{
			this.uniqueConstraints.add(new UniqueConstraintStructure(entityDetails, constraint, fieldMapping));
		}
		
		//Load foreign constraints of the table
		for(ForeignConstraintDetails constraint: entityDetails.getForeignConstraints())
		{
			//if this is mapping constraint and actual relation is
			//		maintained by target entity
			if(constraint.isMappedRelation() || constraint.getJoinTableDetails() != null)
			{
				//don't add this constraint as part of table structure
				continue;
			}
			
			this.foreignConstraints.add(new ForeignConstraintStructure(entityDetails, constraint, fieldMapping, constraint.isDeleteCascaded()));
		}
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public List<ColumnStructure> getColumns()
	{
		return columns;
	}

	public void setColumns(List<ColumnStructure> columns)
	{
		this.columns = columns;
	}

	public List<UniqueConstraintStructure> getUniqueConstraints()
	{
		return uniqueConstraints;
	}

	public void setUniqueConstraints(List<UniqueConstraintStructure> uniqueConstraints)
	{
		this.uniqueConstraints = uniqueConstraints;
	}

	public List<ForeignConstraintStructure> getForeignConstraints()
	{
		return foreignConstraints;
	}

	public void setForeignConstraints(List<ForeignConstraintStructure> foreignConstraints)
	{
		this.foreignConstraints = foreignConstraints;
	}

	public boolean isConstraintsAvailable()
	{
		return (!uniqueConstraints.isEmpty() || !foreignConstraints.isEmpty());
	}

	public boolean isForeignConstraintsAvailable()
	{
		return (!foreignConstraints.isEmpty());
	}
	
	public Map<String, String> getFieldMapping()
	{
		return fieldMapping;
	}
	
	public ColumnStructure getIdColumn()
	{
		return idColumn;
	}
	
	public boolean isSequnceBasedIdColumn()
	{
		return (idColumn != null && idColumn.isSequenceIncrement());
	}

	public boolean isAutoIdColumn()
	{
		return (idColumn != null && idColumn.isAutoIncrement());
	}
}
