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

import java.lang.reflect.Field;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.Extendable;
import com.yukthitech.persistence.repository.annotations.Charset;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Extended table details
 * @author akiran
 */
public class ExtendedTableDetails
{
	/**
	 * Name of the table
	 */
	private String tableName;
	
	/**
	 * Field prefix.
	 */
	private String fieldPrefix;
	
	/**
	 * Number of extended fields.
	 */
	private int fieldCount;
	
	/**
	 * Size fo the field.
	 */
	private int fieldSize;
	
	/**
	 * Character set to be used.
	 */
	private Charset charset;
	
	/**
	 * Entity field which is going to hold extension fields.
	 */
	private Field entityField;
	
	private EntityDetails extendedEntityDetails;
	
	/**
	 * Instantiates a new extended table details.
	 */
	public ExtendedTableDetails()
	{}
	
	/**
	 * Instantiates a new extended table details.
	 *
	 * @param tableName the table name
	 * @param extendable the extendable
	 */
	public ExtendedTableDetails(String tableName, Extendable extendable)
	{
		this.tableName = tableName;
		this.fieldPrefix = extendable.fieldPrefix();
		this.fieldCount = extendable.count();
		this.fieldSize = extendable.fieldSize();
		this.charset = extendable.charset();
	}

	/**
	 * Gets the name of the table.
	 *
	 * @return the name of the table
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * Sets the name of the table.
	 *
	 * @param tableName the new name of the table
	 */
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	/**
	 * Gets the field prefix.
	 *
	 * @return the field prefix
	 */
	public String getFieldPrefix()
	{
		return fieldPrefix;
	}

	/**
	 * Sets the field prefix.
	 *
	 * @param fieldPrefix the new field prefix
	 */
	public void setFieldPrefix(String fieldPrefix)
	{
		this.fieldPrefix = fieldPrefix;
	}

	/**
	 * Gets the number of extended fields.
	 *
	 * @return the number of extended fields
	 */
	public int getFieldCount()
	{
		return fieldCount;
	}

	/**
	 * Sets the number of extended fields.
	 *
	 * @param fieldCount the new number of extended fields
	 */
	public void setFieldCount(int fieldCount)
	{
		this.fieldCount = fieldCount;
	}

	/**
	 * Gets the size fo the field.
	 *
	 * @return the size fo the field
	 */
	public int getFieldSize()
	{
		return fieldSize;
	}

	/**
	 * Sets the size fo the field.
	 *
	 * @param fieldSize the new size fo the field
	 */
	public void setFieldSize(int fieldSize)
	{
		this.fieldSize = fieldSize;
	}

	/**
	 * Gets the character set to be used.
	 *
	 * @return the character set to be used
	 */
	public Charset getCharset()
	{
		return charset;
	}

	/**
	 * Sets the character set to be used.
	 *
	 * @param charset the new character set to be used
	 */
	public void setCharset(Charset charset)
	{
		this.charset = charset;
	}

	/**
	 * Gets the entity field which is going to hold extension fields.
	 *
	 * @return the entity field which is going to hold extension fields
	 */
	public Field getEntityField()
	{
		return entityField;
	}

	/**
	 * Sets the entity field which is going to hold extension fields.
	 *
	 * @param entityField the new entity field which is going to hold extension fields
	 */
	public void setEntityField(Field entityField)
	{
		this.entityField = entityField;
	}
	
	public EntityDetails toEntityDetails(EntityDetails actualEntityDetails)
	{
		if(extendedEntityDetails != null)
		{
			return extendedEntityDetails;
		}
		
		EntityDetails entityDetails = new EntityDetails(tableName, ExtendedTableEntity.class);
		
		Field field = null, entityIdFld = null;
		
		try
		{
			field = ExtendedTableEntity.class.getDeclaredField("dummyField");
			entityIdFld = ExtendedTableEntity.class.getDeclaredField(ExtendedTableEntity.FIELD_ENTITY_ID);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching ExtendedTableEntity fields", ex);
		}

		//add entity id field
		FieldDetails entityIdFldDetails = new FieldDetails(entityIdFld, DataType.LONG, false, false);
		entityIdFldDetails.setDbColumnName(ExtendedTableEntity.COLUMN_ENTITY_ID);

		entityDetails.addFieldDetails(entityIdFldDetails);
		
		//add extended fields
		FieldDetails fieldDetails = null;
		
		for(int i = 0; i < fieldCount; i++)
		{
			fieldDetails = new FieldDetails(field, DataType.STRING, false, true);
			fieldDetails.setDbColumnName(fieldPrefix.toUpperCase() + i);
			fieldDetails.setName(fieldPrefix + i);
			fieldDetails.setLength(fieldSize);
			
			entityDetails.addFieldDetails(fieldDetails);
		}
		
		//add unique constraint on entity id field.
		UniqueConstraintDetails uniqueConstraintDetails = new UniqueConstraintDetails(entityDetails, 
			"UQ_" + actualEntityDetails.getTableName() + "_EXT_ID", 
			new String[]{ExtendedTableEntity.FIELD_ENTITY_ID}, null, false, true);
		
		entityDetails.addUniqueKeyConstraint(uniqueConstraintDetails);
		
		//add foreign constraint details
		ForeignConstraintDetails foreignConstraintDetails = new ForeignConstraintDetails(null, actualEntityDetails, entityIdFld, entityDetails);
		entityDetails.addForeignConstraintDetails(foreignConstraintDetails);
		
		this.extendedEntityDetails = entityDetails;
		
		return entityDetails;
	}
}
