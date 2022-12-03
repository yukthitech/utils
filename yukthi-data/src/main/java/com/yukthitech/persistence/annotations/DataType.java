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
package com.yukthitech.persistence.annotations;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public enum DataType
{
	STRING(String.class), 
	INT(byte.class, Byte.class, short.class, Short.class, int.class, Integer.class), 
	LONG(long.class, Long.class), 
	FLOAT(float.class, Float.class), 
	DOUBLE(double.class, Double.class),
	BOOLEAN(boolean.class, Boolean.class),
	DATE,
	BLOB,
	ZIP_BLOB,
	CLOB,
	DATE_TIME(Date.class), 
	UNKNOWN;
	
	private static Map<Class<?>, DataType> typeMap;
	
	private Class<?> javaTypes[];

	private DataType(Class<?>... javaTypes)
	{
		this.javaTypes = javaTypes;
	}
	
	public static DataType getDataType(Class<?> type)
	{
		if(typeMap != null)
		{
			return typeMap.get(type);
		}
		
		Map<Class<?>, DataType> map = new HashMap<Class<?>, DataType>();
		
		for(DataType dataType: DataType.values())
		{
			for(Class<?> jtype: dataType.javaTypes)
			{
				map.put(jtype, dataType);
			}
		}
		
		DataType.typeMap = map;
		return map.get(type);
	}
	
	public String getName()
	{
		return super.name();
	}
}
