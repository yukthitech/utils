package com.yukthi.persistence.conversion.impl;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.DataTypeMapping;
import com.yukthi.persistence.conversion.IPersistenceConverter;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * This is not a default converter, if needed, this needs to be used explicitly on target entity fields
 * using {@link DataTypeMapping}. 
 * 
 * This converters converts any object into json string. And the converted string is expected to be persisted into db (in VARCHAR, CLOB, BLOB) columns.
 * The converted string will also contain implicit data type which in turn will be used during parsing.
 * 
 * @author akiran
 */
public class JsonWithTypeConverter implements IPersistenceConverter
{
	/**
	 * Object mapper to be used for json to/from object coversaion.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static
	{
		objectMapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
	}

	/* (non-Javadoc)
	 * @see com.fw.persistence.conversion.IPersistenceConverter#convertToJavaType(java.lang.Object, com.fw.persistence.annotations.DataType, java.lang.Class)
	 */
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType)
	{
		if(!(dbObject instanceof String))
		{
			dbObject = toStr(dbObject);
		}
		
		try
		{
			return objectMapper.readValue((String)dbObject, Object.class);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while converting string into java object. Json string: " + dbObject);
		}
	}

	/* (non-Javadoc)
	 * @see com.fw.persistence.conversion.IPersistenceConverter#convertToDBType(java.lang.Object, com.fw.persistence.annotations.DataType)
	 */
	@Override
	public Object convertToDBType(Object javaObject, DataType dbType)
	{
		try
		{
			return objectMapper.writeValueAsString(javaObject);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while converting java object into string. Object: " + javaObject);
		}
	}

	/**
	 * Converts specified db object into string
	 * @param dbObj
	 * @return
	 */
	private static String toStr(Object dbObj)
	{
		//if db object is string
		if(dbObj instanceof String)
		{
			return (String)dbObj;
		}

		//if specified type is blob
		if(dbObj instanceof Blob)
		{
			Blob blob = (Blob)dbObj;
			
			try
			{
				InputStream is = blob.getBinaryStream();
				String res = IOUtils.toString(is);
				
				is.close();
				return res;
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occured while reading blob data.", ex);
			}
		}

		//if db object is byte[]
		if(dbObj instanceof byte[])
		{
			return new String((byte[])dbObj);
		}

		//if db object is clob
		if(dbObj instanceof Clob)
		{
			Clob clob = (Clob)dbObj;
			
			try
			{
				Reader reader = clob.getCharacterStream();
				String res = IOUtils.toString(reader);
				
				reader.close();
				return res;
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occured while reading clob data.", ex);
			}
		}

		throw new IllegalStateException(String.format("Unsupported db data type %s encountered for JSON conversion", dbObj.getClass().getName()));
	}
}
