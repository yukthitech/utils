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
package com.yukthitech.persistence.conversion.impl;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.conversion.IPersistenceConverter;
import com.yukthitech.utils.exceptions.InvalidStateException;

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
	 * Object mapper to be used for json to/from object conversation.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static
	{
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL, As.PROPERTY);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.setTimeZone(TimeZone.getDefault());
	}

	/* (non-Javadoc)
	 * @see com.fw.persistence.conversion.IPersistenceConverter#convertToJavaType(java.lang.Object, com.fw.persistence.annotations.DataType, java.lang.Class)
	 */
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType, Field field)
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
			throw new InvalidStateException("An error occurred while converting string into java object. Json string: " + dbObject, ex);
		}
	}

	/* (non-Javadoc)
	 * @see com.fw.persistence.conversion.IPersistenceConverter#convertToDBType(java.lang.Object, com.fw.persistence.annotations.DataType)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object convertToDBType(Object javaObject, DataType dbType)
	{
		if(javaObject instanceof Enum)
		{
			Enum<?> en = (Enum<?>) javaObject;
			return String.format("[\"%s\", \"%s\"]", ((Enum) javaObject).getDeclaringClass().getName(), en.name());
		}
		
		try
		{
			return objectMapper.writeValueAsString(javaObject);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting java object into string. Object: " + javaObject, ex);
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
				String res = IOUtils.toString(is, Charset.defaultCharset());
				
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

		if(dbObj instanceof char[])
		{
			return new String((char[])dbObj);
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
