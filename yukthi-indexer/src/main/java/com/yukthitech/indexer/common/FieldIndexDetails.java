package com.yukthitech.indexer.common;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import com.yukthitech.indexer.IndexField;
import com.yukthitech.indexer.IndexType;

/**
 * Index details of a field of indexable type.
 * @author akiran
 */
public class FieldIndexDetails
{
	/**
	 * Indexing to be used.
	 */
	private IndexType indexType;
	
	/**
	 * ES data type name.
	 */
	private DataType dataType;
	
	/**
	 * Indicates if this id field or not.
	 */
	private boolean idField;
	
	/**
	 * Flag to indicate if case should be ignored for this field
	 */
	private boolean ignoreCase;
	
	/**
	 * Subfields for object field type.
	 */
	private Map<String, FieldIndexDetails> subfields;
	
	private Field field;
	
	public FieldIndexDetails(Field field, DataType esDataType, IndexField indexField)
	{
		this.field = field;
		this.indexType = indexField.value();
		this.dataType = esDataType;
		
		this.ignoreCase = indexField.ignoreCase();
		this.idField = indexField.idField();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return field.getName();
	}
	
	/**
	 * Gets the field.
	 *
	 * @return the field
	 */
	public Field getField()
	{
		return field;
	}

	/**
	 * Gets the index type.
	 *
	 * @return the index type
	 */
	public IndexType getIndexType()
	{
		return indexType;
	}
	
	/**
	 * Gets the es data type.
	 *
	 * @return the es data type
	 */
	public DataType getDataType()
	{
		return dataType;
	}
	
	public boolean isIgnoreCase()
	{
		return ignoreCase;
	}
	
	/**
	 * Checks if is indicates if this id field or not.
	 *
	 * @return the indicates if this id field or not
	 */
	public boolean isIdField()
	{
		return idField;
	}
	
	void setSubfields(Map<String, FieldIndexDetails> subfields)
	{
		this.subfields = subfields;
	}
	
	public Collection<FieldIndexDetails> getSubfields()
	{
		if(subfields == null)
		{
			return null;
		}
		
		return subfields.values();
	}
	
	public Map<String, FieldIndexDetails> getSubfieldMap()
	{
		return subfields;
	}
}
