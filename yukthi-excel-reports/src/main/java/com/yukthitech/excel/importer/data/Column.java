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
package com.yukthitech.excel.importer.data;

public class Column
{
	private String name;
	private ColumnType type;
	private Class<?> javaType;
	private String format;

	public Column(String name, ColumnType type, Class<?> javaType, String format)
	{
		if(name == null || name.trim().length() == 0)
		{
			throw new NullPointerException("Name can not be null or empty");
		}
		
		if(type == null)
		{
			throw new NullPointerException("Type can not be null");
		}
		
		this.name = name;
		this.type = type;
		this.javaType = javaType;
		this.format = format;
	}

	public String getName()
	{
		return name;
	}

	public ColumnType getType()
	{
		return type;
	}

	public Class<?> getJavaType()
	{
		return javaType;
	}
	
	public String getFormat()
	{
		return format;
	}
}
