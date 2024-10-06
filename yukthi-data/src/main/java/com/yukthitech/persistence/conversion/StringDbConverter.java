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
package com.yukthitech.persistence.conversion;

import java.lang.reflect.Field;
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
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType, Field field)
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
