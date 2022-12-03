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

import java.sql.Timestamp;
import java.util.Date;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.conversion.IPersistenceConverter;

public class DateConverter implements IPersistenceConverter
{
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType)
	{
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
