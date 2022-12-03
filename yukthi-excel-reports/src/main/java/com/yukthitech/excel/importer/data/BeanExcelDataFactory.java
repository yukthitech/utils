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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BeanExcelDataFactory<T> implements IExcelDataFactory<T>
{
	private static Logger logger = LogManager.getLogger(BeanExcelDataFactory.class);

	public static class FieldColumn extends Column
	{
		private Field field;
		
		public FieldColumn(String name, ColumnType type, Field field, ExcelConfig excelLabel)
		{
			super(name, type, field.getType(), (excelLabel == null) ? null : excelLabel.format());
			this.field = field;
		}

		public Field getField()
		{
			return field;
		}
		
		@Override
		public String toString()
		{
			return super.toString() + "[Name: " + super.getName() + ", Field: " + field.getName() + "]";
		}
	}
	
	private Class<T> beanType;
	private Map<String, FieldColumn> columns = new HashMap<>();
	
	public BeanExcelDataFactory(Class<T> beanType)
	{
		this.beanType = beanType;
		
		Field fields[] = beanType.getDeclaredFields();
		ExcelConfig excelConfig = null;
		String label = null;
		ColumnType columnType = null;
		
		for(Field field: fields)
		{
			if(Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}
			
			excelConfig = field.getAnnotation(ExcelConfig.class);
			
			if(excelConfig != null && excelConfig.ignore())
			{
				continue;
			}
			
			columnType = getColumnType(field.getType());
			
			if(columnType == null)
			{
				logger.debug("Ignoring field. Unsupported data-type '{}' encountered on field - {}", field.getType().getName(), field.getName());
				continue;
			}

			label = field.getName();
			
			if(excelConfig != null && StringUtils.isNotBlank(excelConfig.label()))
			{
				label = excelConfig.label();
			}
			
			label = label.toLowerCase();
			
			//remove all non word characters and make into single word
			label = label.replaceAll("[\\W\\_]+", "");
			
			this.columns.put(label, new FieldColumn(label, columnType, field, excelConfig));
		}
	}
	
	private ColumnType getColumnType(Class<?> javaType)
	{
		if(Byte.class.equals(javaType) || byte.class.equals(javaType))
		{
			return ColumnType.INTEGER;
		}

		if(Short.class.equals(javaType) || short.class.equals(javaType))
		{
			return ColumnType.INTEGER;
		}

		if(Integer.class.equals(javaType) || int.class.equals(javaType))
		{
			return ColumnType.INTEGER;
		}

		if(Float.class.equals(javaType) || float.class.equals(javaType))
		{
			return ColumnType.FLOAT;
		}

		if(Double.class.equals(javaType) || double.class.equals(javaType))
		{
			return ColumnType.FLOAT;
		}

		if(Boolean.class.equals(javaType) || boolean.class.equals(javaType))
		{
			return ColumnType.BOOLEAN;
		}

		if(String.class.equals(javaType))
		{
			return ColumnType.STRING;
		}

		if(Date.class.equals(javaType))
		{
			return ColumnType.DATE;
		}
		
		if(Enum.class.isAssignableFrom(javaType))
		{
			return ColumnType.ENUM;
		}

		return null;
	}
	
	@Override
	public Collection<FieldColumn> getColumns()
	{
		return columns.values();
	}
	
	public Class<?> getBeanType()
	{
		return beanType;
	}
	
	@Override
	public Column getColumn(String name)
	{
		return this.columns.get(name);
	}

	@Override
	public T newDataObject(Map<String, Object> valueMap)
	{
		try
		{
			T bean = beanType.newInstance();
			Object value = null;
			boolean accessible = false;
			
			for(FieldColumn column: this.columns.values())
			{
				value = valueMap.get(column.getName());
				
				if(value == null)
				{
					continue;
				}
				
				accessible = column.field.isAccessible();
				
				column.field.setAccessible(true);
				column.field.set(bean, value);
				
				column.field.setAccessible(accessible);
			}
			
			return bean;
		}catch(Exception ex)
		{
			logger.error("An error occurred while creating excel bean", ex);
			throw new IllegalStateException("An error occurred while creating excel bean", ex);
		}	
	}
	
	@Override
	public boolean isHeadingRow(List<String> row)
	{
		return this.columns.keySet().containsAll(row);
	}
}
