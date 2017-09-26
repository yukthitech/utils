package com.yukthitech.autox.storage;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;

/**
 * The Class DataTableEntity.
 */
@Table(name = "AUTOX_DATA_TABLE")
public class DataTableEntity
{
	/**
	 * Primary key.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/**
	 * key of the entry.
	 */
	@Column(name = "DATA_KEY", length = 100, unique = true)
	private String key;
	
	/**
	 * value of the entry.
	 */
	@DataTypeMapping(type = DataType.CLOB, converterType = JsonWithTypeConverter.class)
	@Column(name = "DATA_VALUE")
	private Object value;
	
	public DataTableEntity()
	{}
	
	public DataTableEntity(String key, Object value)
	{
		this.key = key;
		this.value = value;
	}

	/**
	 * Gets the primary key.
	 *
	 * @return the primary key
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets the primary key.
	 *
	 * @param id the new primary key
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * Gets the key of the entry.
	 *
	 * @return the key of the entry
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * Sets the key of the entry.
	 *
	 * @param key the new key of the entry
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	/**
	 * Gets the value of the entry.
	 *
	 * @return the value of the entry
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the entry.
	 *
	 * @param value the new value of the entry
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
}
