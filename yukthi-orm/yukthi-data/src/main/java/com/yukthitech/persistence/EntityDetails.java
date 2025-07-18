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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.exceptions.InvalidConfigurationException;

public class EntityDetails
{
	private static Logger logger = LogManager.getLogger(EntityDetails.class);
	
	public static final String COL_UQ_ENTITY_ID = "UQ_ENTITY_ID";
	
	private String tableName;
	private Class<?> entityType;

	private Map<String, FieldDetails> fieldToDetails = new HashMap<>();
	private Map<String, FieldDetails> columnToDetails = new HashMap<>();

	private List<UniqueConstraintDetails> uniqueConstraints = new ArrayList<>();
	private Set<String> uniqueConstraintFields = new HashSet<>();

	private List<ForeignConstraintDetails> foreignConstraints = new ArrayList<>();

	private List<ForeignConstraintDetails> childConstraints = new ArrayList<>();

	private List<IndexDetails> indexDetailsList = new ArrayList<>();

	private FieldDetails idField;
	
	/**
	 * Field used to maintain entity version for optimistic updates
	 */
	private FieldDetails versionField;
	
	/**
	 * Extended table details for this entity.
	 */
	private ExtendedTableDetails extendedTableDetails;
	
	/**
	 * Indicates whether table is created
	 */
	private boolean isTableCreated = false;
	
	/**
	 * Short name to be used for this entity.
	 */
	private String shortName;
	
	/**
	 * Full column names of the tables. Some of which may not have mapping to fields.
	 */
	private Set<String> fullColumnNames = new HashSet<>();

	public EntityDetails(String tableName, Class<?> entityType)
	{
		if(tableName == null || tableName.trim().length() == 0)
		{
			throw new NullPointerException("Table-name can not be null or empty");
		}

		if(entityType == null)
		{
			throw new NullPointerException("Entity-type can not be null");
		}

		this.tableName = tableName;
		this.entityType = entityType;
	}

	void resetColumnMapping(Map<String, String> fieldMapping)
	{
		columnToDetails.clear();

		FieldDetails fieldDetails = null;
		String column = null;

		for(String name : fieldToDetails.keySet())
		{
			fieldDetails = fieldToDetails.get(name);
			column = fieldMapping.get(name);

			if(column == null)
			{
				if(!fieldDetails.isTableOwned())
				{
					continue;
				}
				
				throw new IllegalArgumentException("No field-mapping found for field: " + name);
			}

			fieldDetails.setDbColumnName(column);
			columnToDetails.put(column.toLowerCase(), fieldDetails);
		}
	}
	
	/**
	 * Gets the short name to be used for this entity.
	 *
	 * @return the short name to be used for this entity
	 */
	public String getShortName()
	{
		if(shortName != null)
		{
			return shortName;
		}
		
		String shortName = entityType.getSimpleName();
		
		//for join tables using table name
		if(JoinTableEntity.class.equals(entityType))
		{
			shortName = tableName;
		}
		
		if(shortName.toLowerCase().endsWith("entity"))
		{
			shortName = shortName.substring(0, shortName.length() - "entity".length());
		}
		
		if(shortName.trim().length() == 0)
		{
			shortName = "T";
		}
		
		this.shortName = shortName;
		return shortName;
	}

	public String getTableName()
	{
		return tableName;
	}

	public Class<?> getEntityType()
	{
		return entityType;
	}

	void addFieldDetails(FieldDetails fieldDetails)
	{
		if(fieldToDetails.containsKey(fieldDetails.getName()))
		{
			logger.warn("Field '" + fieldDetails.getName() + "' is mapped multiple times");
			//throw new InvalidMappingException("Field '" + fieldDetails.getField() + "' is mapped multiple times");
		}
		
		String columnName = fieldDetails.getDbColumnName().toLowerCase();
		columnName = (columnName != null) ? columnName.toLowerCase() : null;
		
		addColumn(columnName);

		if(columnToDetails.containsKey(columnName))
		{
			throw new InvalidConfigurationException("Column '{}' is mapped by multiple fields - [{}, {}] in entity - {}", 
					fieldDetails.getDbColumnName(), fieldDetails.getName(), 
					columnToDetails.get(columnName).getName(), 
					entityType.getName() );
		}

		fieldToDetails.put(fieldDetails.getName(), fieldDetails);
		
		//if column name is present, this might be case with non-owned relation fields
		if(columnName != null)
		{
			columnToDetails.put(columnName, fieldDetails);
		}

		if(fieldDetails.isIdField())
		{
			if(this.idField != null)
			{
				throw new InvalidMappingException("Multiple id fields are defined for entity: " + entityType.getName());
			}

			this.idField = fieldDetails;
		}
		
		if(fieldDetails.isVersionField())
		{
			if(this.versionField != null)
			{
				throw new InvalidMappingException("Multiple version fields are defined for entity: " + entityType.getName());
			}

			this.versionField = fieldDetails;
		}
	}

	public FieldDetails getFieldDetailsByField(String field)
	{
		return fieldToDetails.get(field);
	}

	public FieldDetails getFieldDetailsByColumn(String column)
	{
		return columnToDetails.get(column.toLowerCase());
	}

	public Collection<FieldDetails> getFieldDetails()
	{
		return fieldToDetails.values();
	}

	public boolean hasField(String name)
	{
		return fieldToDetails.containsKey(name);
	}

	void addUniqueKeyConstraint(UniqueConstraintDetails uniqueConstraintDetails)
	{
		if(uniqueConstraintDetails == null)
		{
			throw new NullPointerException("Unique constraint details can not be null");
		}

		if(uniqueConstraintFields.contains(uniqueConstraintDetails.getFieldsString()))
		{
			throw new InvalidMappingException("Multile unique constraints defined on same fields: " + uniqueConstraintDetails.getFieldsString());
		}

		this.uniqueConstraints.add(uniqueConstraintDetails);
		this.uniqueConstraintFields.add(uniqueConstraintDetails.getFieldsString());
	}

	public Collection<UniqueConstraintDetails> getUniqueConstraints()
	{
		return Collections.unmodifiableCollection(uniqueConstraints);
	}

	void addForeignConstraintDetails(ForeignConstraintDetails foreignConstraintDetails)
	{
		if(foreignConstraintDetails == null)
		{
			throw new NullPointerException("Unique constraint details can not be null");
		}

		this.foreignConstraints.add(foreignConstraintDetails);
	}

	public Collection<ForeignConstraintDetails> getForeignConstraints()
	{
		return Collections.unmodifiableCollection(foreignConstraints);
	}

	public FieldDetails getIdField()
	{
		return idField;
	}

	public boolean hasIdField()
	{
		return (idField != null);
	}
	
	/**
	 * Gets the field used to maintain entity version for optimistic updates.
	 *
	 * @return the field used to maintain entity version for optimistic updates
	 */
	public FieldDetails getVersionField()
	{
		return versionField;
	}
	
	public boolean hasVersionField()
	{
		return (versionField != null);
	}

	/**
	 * Gets the unique constraints which involves specified field
	 * @param field
	 * @return
	 */
	public List<UniqueConstraintDetails> getUniqueConstraints(String field)
	{
		List<UniqueConstraintDetails> constraints = null;

		for(UniqueConstraintDetails constraint : this.uniqueConstraints)
		{
			if(!constraint.hasField(field))
			{
				continue;
			}

			if(constraints == null)
			{
				constraints = new ArrayList<>();
			}

			constraints.add(constraint);
		}

		return constraints;
	}

	/** 
	 * Adds value to {@link #childConstraints Child Constraints}
	 *
	 * @param childConstraint childConstraint to be added
	 */
	public void addChildConstraint(ForeignConstraintDetails childConstraint)
	{
		//if specified constraint is owned by current entity, ignore child end relation
		if(childConstraint.isMappedRelation())
		{
			return;
		}
		
		if(childConstraints == null)
		{
			childConstraints = new ArrayList<ForeignConstraintDetails>();
		}

		childConstraints.add(childConstraint);
	}

	public List<ForeignConstraintDetails> getChildConstraints()
	{
		return Collections.unmodifiableList(childConstraints);
	}

	/** 
	 * Adds value to {@link #indexDetailsList Index Details List}
	 *
	 * @param indexDetails indexDetails to be added
	 */
	public void addIndexDetails(IndexDetails indexDetails)
	{
		for(IndexDetails oldIndex : this.indexDetailsList)
		{
			if(oldIndex.getName().equals(indexDetails.getName()))
			{
				throw new InvalidPersistenceConfigurationException("Duplicate index name encountered - " + indexDetails.getName());
			}

			if(Arrays.equals(oldIndex.getFields(), indexDetails.getFields()))
			{
				throw new InvalidPersistenceConfigurationException("Multiple indexes are defined on same fields: " + Arrays.asList(indexDetails.getFields()));
			}
		}

		indexDetailsList.add(indexDetails);
	}

	public List<IndexDetails> getIndexDetailsList()
	{
		return indexDetailsList;
	}

	public Set<String> getColumns()
	{
		return Collections.unmodifiableSet(columnToDetails.keySet());
	}
	
	/**
	 * @return the {@link #isTableCreated isTableCreated}
	 */
	public boolean isTableCreated()
	{
		return isTableCreated;
	}

	/**
	 * @param isTableCreated the {@link #isTableCreated isTableCreated} to set
	 */
	public void setTableCreated(boolean isTableCreated)
	{
		this.isTableCreated = isTableCreated;
	}
	
	/**
	 * Gets the extended table details for this entity.
	 *
	 * @return the extended table details for this entity
	 */
	public ExtendedTableDetails getExtendedTableDetails()
	{
		return extendedTableDetails;
	}

	/**
	 * Sets the extended table details for this entity.
	 *
	 * @param extendedTableDetails the new extended table details for this entity
	 */
	public void setExtendedTableDetails(ExtendedTableDetails extendedTableDetails)
	{
		this.extendedTableDetails = extendedTableDetails;
	}
	
	public void addColumn(String column)
	{
		if(column == null)
		{
			return;
		}
		
		this.fullColumnNames.add(column.toLowerCase());
	}
	
	public boolean hasColumn(String column)
	{
		return fullColumnNames.contains(column.toLowerCase());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Table: ").append(tableName);
		builder.append(",").append("Type: ").append(entityType);

		builder.append("]");
		return builder.toString();
	}
}
