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
package com.yukthitech.persistence.rdbms.converters;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.Clob;

import org.apache.commons.io.IOUtils;

import com.yukthitech.persistence.LobData;
import com.yukthitech.persistence.UnsupportedOperationException;
import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.conversion.IPersistenceConverter;
import com.yukthitech.utils.exceptions.InvalidStateException;


/**
 * CLOB conversion handler
 * @author akiran
 */
public class ClobConverter implements IPersistenceConverter
{
	/* (non-Javadoc)
	 * @see com.fw.persistence.conversion.IPersistenceConverter#convertToJavaType(java.lang.Object, com.fw.persistence.annotations.DataType, java.lang.Class)
	 */
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType, Field field)
	{
		//if db type is not clob, don't try conversion
		if(dbType != DataType.CLOB)
		{
			return null;
		}
		
		//if target java type is file type
		if(File.class.equals(javaType))
		{
			return convertToFile(dbObject);
		}

		//if db object is char[] and target is string
		if(dbObject instanceof char[])
		{
			return new String((char[])dbObject);
		}

		//if db object is clob and target is string
		if(dbObject instanceof Clob)
		{
			Clob clob = (Clob)dbObject;
			
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

		throw new UnsupportedOperationException(String.format("Unsupported db object type '%s' encountered during CLOB to java conversion", dbObject.getClass().getName()));
	}

	
	private File convertToFile(Object dbObject)
	{
		Reader reader = null;
		
		try
		{
			if(dbObject instanceof char[])
			{
				reader = new CharArrayReader((char[])dbObject);
			}
			else if(dbObject instanceof String)
			{
				reader = new StringReader((String)dbObject);
			}
			else if(dbObject instanceof Clob)
			{
				reader = ((Clob)dbObject).getCharacterStream();			
			}
			else
			{
				throw new UnsupportedOperationException("Unsupported Clob db object encountered - " + dbObject.getClass().getName());
			}
	
			File tempFile = File.createTempFile("temp", ".tmp");
			FileWriter fos = new FileWriter(tempFile);
			IOUtils.copy(reader, fos);
			fos.close();
			reader.close();
			
			return tempFile;
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while create temp file from blob");
		}
	}

	/* (non-Javadoc)
	 * @see com.fw.persistence.conversion.IPersistenceConverter#convertToDBType(java.lang.Object, com.fw.persistence.annotations.DataType)
	 */
	@Override
	public Object convertToDBType(Object javaObject, DataType dbType)
	{
		//if not clob field, ignore
		if(dbType != DataType.CLOB)
		{
			return null;
		}
		
		//if java object instance of file, return lob data
		if(javaObject instanceof File)
		{
			return new LobData((File)javaObject, true);
		}
		
		//ensure java type is string
		if(!(javaObject instanceof String))
		{
			throw new UnsupportedOperationException(String.format("Non-string object is encountered for CLOB storage - {}", javaObject));
		}

		return javaObject;
	}

}
