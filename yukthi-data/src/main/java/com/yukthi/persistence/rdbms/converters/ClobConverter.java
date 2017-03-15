package com.yukthi.persistence.rdbms.converters;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;

import org.apache.commons.io.IOUtils;

import com.yukthi.persistence.LobData;
import com.yukthi.persistence.UnsupportedOperationException;
import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.conversion.IPersistenceConverter;
import com.yukthi.utils.exceptions.InvalidStateException;


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
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType)
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
