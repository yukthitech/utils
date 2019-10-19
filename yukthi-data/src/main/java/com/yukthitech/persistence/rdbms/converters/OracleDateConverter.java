package com.yukthitech.persistence.rdbms.converters;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.conversion.IPersistenceConverter;
import com.yukthitech.utils.exceptions.InvalidStateException;

import oracle.sql.TIMESTAMP;

public class OracleDateConverter implements IPersistenceConverter
{
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType)
	{
		if(dbObject instanceof TIMESTAMP)
		{
			try
			{
				dbObject = ((TIMESTAMP) dbObject).dateValue();
			}catch(SQLException ex)
			{
				throw new InvalidStateException("An error occurred while converting oracle timestamp to date: {}", dbObject, ex);
			}
		}
		
		if((dbObject instanceof Date) && Date.class.equals(javaType))
		{
			return new Date( ((Date) dbObject).getTime() );
		}
		
		return dbObject;
	}

	@Override
	public Object convertToDBType(Object javaObject, DataType dbType)
	{
		if(dbType == DataType.DATE_TIME && (javaObject instanceof Date))
		{
			return new Timestamp( ((Date) javaObject).getTime() );
		}
		
		if(dbType == DataType.DATE && (javaObject instanceof Date))
		{
			return new java.sql.Date( ((Date) javaObject).getTime() );
		}

		return null;
	}
}
