package com.yukthitech.persistence.query.data;

import java.lang.reflect.Field;

import javax.persistence.GenerationType;

import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.PersistenceException;
import com.yukthitech.persistence.annotations.DataType;

public class ColumnStructure
{
	private String name;
	private int length = 255;
	private DataType type;
	private boolean nullable = true;
	private boolean autoIncrement;
	private boolean sequenceIncrement;
	private boolean idField;
	
	private String sequenceName;
	
	public ColumnStructure(Class<?> entityType, FieldDetails fieldDetails)
	{
		Field field = fieldDetails.getField();
		
		this.autoIncrement = (fieldDetails.isIdField() && fieldDetails.getGenerationType() == GenerationType.IDENTITY);
		this.sequenceIncrement = (fieldDetails.isIdField() && fieldDetails.getGenerationType() == GenerationType.SEQUENCE);
		
		if(this.sequenceIncrement)
		{
			this.sequenceName = fieldDetails.getSequenceName();
		}
		
		this.idField = fieldDetails.isIdField();
		this.name = fieldDetails.getDbColumnName();
		
		this.length = fieldDetails.getLength();
		this.type = fieldDetails.getDbDataType();
		this.nullable = fieldDetails.isNullable();
		
		if(this.type == DataType.UNKNOWN)
		{
			this.type = null;
		}
		
		if(this.type == null || this.type == DataType.UNKNOWN)
		{
			Class<?> fieldType = field.getType();
			
			//if the field is relation field
			if(fieldDetails.isRelationField())
			{
				//get column type from target entity id field type
				this.type = DataType.getDataType(fieldDetails.getForeignConstraintDetails().getTargetEntityDetails().getIdField().getField().getType());
			}
			//if current field is normal field
			else
			{
				//get column type based on field's java type
				this.type = DataType.getDataType(fieldType);
			}
			
			if(this.type == null)
			{
				if(fieldDetails.getDbDataType() == null)
				{
					throw new PersistenceException("Unsupported data type '" + fieldType.getName() + "' encountered. [Field: " + field.getName() + ", Enttity Type: " + entityType.getName());
				}
				
				this.type = fieldDetails.getDbDataType();
			}
		}
		
		//for version fields, ensure it is not nullable
		if(fieldDetails.isVersionField())
		{
			this.nullable = false;
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public DataType getType()
	{
		return type;
	}

	public void setType(DataType type)
	{
		this.type = type;
	}
	
	public String getTypeName()
	{
		return type.getName();
	}

	public boolean isNullable()
	{
		return nullable;
	}

	public void setNullable(boolean nullable)
	{
		this.nullable = nullable;
	}

	public boolean isAutoIncrement()
	{
		return autoIncrement;
	}
	
	public boolean isSequenceIncrement()
	{
		return sequenceIncrement;
	}
	
	public boolean isIdField()
	{
		return idField;
	}
	
	public String getSequenceName()
	{
		return sequenceName;
	}
}
