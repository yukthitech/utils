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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import com.yukthitech.persistence.annotations.DataType;

/**
 * Converts sql date types into util date.
 * @author akiran
 */
public class DateConverter implements IPersistenceConverter
{
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType)
	{
		if(!(Date.class.equals(javaType)))
		{
			return null;
		}
		
		if(dbObject instanceof Date)
		{
			return (Date) dbObject;
		}
		
		if(dbObject instanceof LocalDateTime)
		{
			LocalDateTime localDateTime = (LocalDateTime) dbObject;
			return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		}
		
		if(dbObject instanceof LocalTime)
		{
			LocalTime localTime = (LocalTime) dbObject;
			Instant instant = localTime
					.atDate(LocalDate.now())
					.atZone(ZoneId.systemDefault())
					.toInstant();
			
			return Date.from(instant);
		}
		
		if(dbObject instanceof LocalDate)
		{
			LocalDate localDate = (LocalDate) dbObject;
			Instant instant = localDate
					.atTime(LocalTime.now())
					.atZone(ZoneId.systemDefault())
					.toInstant();
			
			return Date.from(instant);
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
