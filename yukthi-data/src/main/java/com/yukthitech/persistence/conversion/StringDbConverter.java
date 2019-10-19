package com.yukthitech.persistence.conversion;

import java.util.Collection;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.utils.OrmUtils;

/**
 * Converts different data types into string, for string db data type
 * @author akiran
 */
public class StringDbConverter implements IPersistenceConverter
{
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType)
	{
		//return null, so that default behaviour is retained
		return null;
	}

	@Override
	public Object convertToDBType(Object javaObject, DataType dbType)
	{
		//for non string ignore
		if(dbType != DataType.STRING)
		{
			return null;
		}
		
		//simple convert java object to string using to-string
		return convertToString(javaObject);
	}

	@SuppressWarnings("unchecked")
	private Object convertToString(Object javaObj)
	{
		if(!(javaObj instanceof Collection))
		{
			return javaObj.toString();
		}
		
		Collection<Object> inCollection = (Collection<Object>) javaObj;
		Collection<Object> resCollection = OrmUtils.createCollection(javaObj.getClass());
		
		for(Object obj : inCollection)
		{
			resCollection.add(obj.toString());
		}
		
		return resCollection;
	}
}
