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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.sql.Blob;

import org.apache.commons.io.IOUtils;

import com.yukthitech.persistence.LobData;
import com.yukthitech.persistence.UnsupportedOperationException;
import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.conversion.IPersistenceConverter;
import com.yukthitech.utils.ZipUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;


/**
 * BLOB conversion handler
 * @author akiran
 */
public class BlobConverter implements IPersistenceConverter
{
	
	/**
	 * Reads object from specified input stream and returns the same
	 * @param is
	 * @return
	 * @throws ClassNotFoundException 
	 */
	private Object readObject(InputStream is) throws IOException, ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(is);
		Object res = ois.readObject();
		
		ois.close();
		is.close();
		
		return res;
	}
	
	private File convertToFile(Object dbObject)
	{
		InputStream is = null;
		
		try
		{
			if(dbObject instanceof byte[])
			{
				is = new ByteArrayInputStream((byte[])dbObject);
			}
			else if(dbObject instanceof Blob)
			{
				is = ((Blob)dbObject).getBinaryStream();			
			}
			else
			{
				throw new UnsupportedOperationException("Unsupported blob db object encountered - " + dbObject.getClass().getName());
			}
	
			File tempFile = File.createTempFile("temp", ".tmp");
			FileOutputStream fos = new FileOutputStream(tempFile);
			IOUtils.copy(is, fos);
			fos.close();
			is.close();
			
			return tempFile;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while create temp file from blob", ex);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.fw.persistence.conversion.IPersistenceConverter#convertToJavaType(java.lang.Object, com.fw.persistence.annotations.DataType, java.lang.Class)
	 */
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType, Field field)
	{
		//if db type is not blob, don't try conversion
		if(dbType != DataType.BLOB && dbType != DataType.ZIP_BLOB)
		{
			return null;
		}

		//if target java type is file type
		if(File.class.equals(javaType))
		{
			File file = convertToFile(dbObject);
			
			if(dbType == DataType.ZIP_BLOB)
			{
				File oldFile = file;
				file = ZipUtils.unzipFile(file);
				oldFile.delete();
			}
			
			return file;
		}
		
		//if db object is char[] and target is string
		if(dbObject instanceof byte[])
		{
			try
			{
				byte data[] = (byte[])dbObject;
				
				if(dbType == DataType.ZIP_BLOB)
				{
					data = ZipUtils.unzipBytes(data);
				}
				
				if(byte[].class.equals(javaType))
				{
					return data;
				}
				
				return readObject(new ByteArrayInputStream(data));
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while reading object from blob", ex);
			}
		}

		//if db object is blob and target is string
		if(dbObject instanceof Blob)
		{
			Blob blob = (Blob)dbObject;
			
			try
			{
				byte data[] = IOUtils.toByteArray(blob.getBinaryStream());
				
				if(dbType == DataType.ZIP_BLOB)
				{
					data = ZipUtils.unzipBytes(data);
				}
				
				if(byte[].class.equals(javaType))
				{
					return data;
				}

				return readObject(blob.getBinaryStream());
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while reading object from blob", ex);
			}
		}

		throw new UnsupportedOperationException(String.format("Unsupported db object type '%s' encountered during BLOB to java conversion", dbObject.getClass().getName()));
	}

	/* (non-Javadoc)
	 * @see com.fw.persistence.conversion.IPersistenceConverter#convertToDBType(java.lang.Object, com.fw.persistence.annotations.DataType)
	 */
	@Override
	public Object convertToDBType(Object javaObject, DataType dbType)
	{
		//if not blob field, ignore
		if(dbType != DataType.BLOB && dbType != DataType.ZIP_BLOB)
		{
			return null;
		}
		
		//if java object instance of file, return lob data
		if(javaObject instanceof File)
		{
			File file = (File) javaObject;
			
			if(dbType == DataType.ZIP_BLOB)
			{
				file = ZipUtils.zipFile(file);
			}
			
			return new LobData(file, false);
		}
		
		if(javaObject instanceof byte[])
		{
			return new LobData((byte[]) javaObject);
		}
			
		//convert java object into byte[]
		byte dataBytes[] = toBytes(javaObject);
		
		if(dbType == DataType.ZIP_BLOB)
		{
			dataBytes = ZipUtils.zipBytes(dataBytes);
		}
		
		return new LobData((byte[]) dataBytes);
	}
	
	/**
	 * Converts object into bytes.
	 * @param object object to be converted.
	 * @return converted bytes.
	 */
	private byte[] toBytes(Object object)
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			
			oos.writeObject(object);
			oos.flush();
			
			oos.close();
			bos.flush();
			
			return bos.toByteArray();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while converting specified java object into BLOB - " + object, ex);
		}
	}
}
