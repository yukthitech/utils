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
import java.math.BigDecimal;

import com.yukthitech.persistence.annotations.DataType;

/**
 * Converts numeric db values into {@link BigDecimal}.
 * @author akiran
 */
public class DecimalConverter implements IPersistenceConverter
{
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType, Field field)
	{
		if(!(BigDecimal.class.equals(javaType)))
		{
			return null;
		}
		
		if(dbObject instanceof BigDecimal)
		{
			return dbObject;
		}
		
		if(dbObject instanceof Number)
		{
			return new BigDecimal(dbObject.toString());
		}
		
		if(dbObject instanceof String)
		{
			String str = ((String) dbObject).trim();
			
			if(str.length() == 0)
			{
				return null;
			}
			
			return new BigDecimal(str);
		}

		//return null, so that default behaviour is retained
		return null;
	}

	@Override
	public Object convertToDBType(Object javaObject, DataType dbType)
	{
		return null;
	}
}
