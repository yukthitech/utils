package com.yukthi.persistence.rdbms.converters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;

import org.apache.commons.io.IOUtils;

import com.yukthi.persistence.LobData;
import com.yukthi.persistence.UnsupportedOperationException;
import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.conversion.IPersistenceConverter;
import com.yukthi.utils.exceptions.InvalidStateException;


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
			throw new InvalidStateException(ex, "An error occurred while create temp file from blob");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.fw.persistence.conversion.IPersistenceConverter#convertToJavaType(java.lang.Object, com.fw.persistence.annotations.DataType, java.lang.Class)
	 */
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType)
	{
		//if db type is not blob, don't try conversion
		if(dbType != DataType.BLOB)
		{
			return null;
		}

		//if target java type is file type
		if(File.class.equals(javaType))
		{
			return convertToFile(dbObject);
		}
		
		//if db object is char[] and target is string
		if(dbObject instanceof byte[])
		{
			try
			{
				return readObject(new ByteArrayInputStream((byte[])dbObject));
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
		if(dbType != DataType.BLOB)
		{
			return null;
		}
		
		//if java object instance of file, return lob data
		if(javaObject instanceof File)
		{
			return new LobData((File)javaObject, false);
		}
			
		//convert java object into byte[]
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			
			oos.writeObject(javaObject);
			oos.flush();
			
			oos.close();
			bos.flush();
			
			return bos.toByteArray();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while converting specified java object into BLOB - " + javaObject, ex);
		}
	}

}
