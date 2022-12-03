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
package com.yukthitech.indexer.common;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.yukthitech.indexer.IndexField;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;

/**
 * Index details of a type.
 * @author akiran
 */
public class TypeIndexDetails
{
	private static Map<Class<?>, DataType> supportedTypes;
	
	static
	{
		supportedTypes = CommonUtils.toMap(
			byte.class, DataType.BYTE,
			Byte.class, DataType.BYTE,
			
			short.class, DataType.SHORT,
			Short.class, DataType.SHORT,

			int.class, DataType.INTEGER,
			Integer.class, DataType.INTEGER,

			long.class, DataType.LONG,
			Long.class, DataType.LONG,

			float.class, DataType.FLOAT,
			Float.class, DataType.FLOAT,

			double.class, DataType.DOUBLE,
			Double.class, DataType.DOUBLE,

			boolean.class, DataType.BOOLEAN,
			Boolean.class, DataType.BOOLEAN,

			Date.class, DataType.DATE,
			
			String.class, DataType.STRING
		);
	}
	
	/**
	 * Index details of fields of index type.
	 */
	private Map<String, FieldIndexDetails> fields = new HashMap<>();
	
	/**
	 * Index type.
	 */
	private Class<?> type;
	
	/**
	 * Loads index details of specified type.
	 * @param type
	 */
	public TypeIndexDetails(Class<?> type)
	{
		this.type = type;
		this.fetchFields(type, fields);
	}
	
	private void fetchFields(Class<?> type, Map<String, FieldIndexDetails> fieldMap)
	{
		Field fields[] = type.getDeclaredFields();
		IndexField indexField = null;
		
		Class<?> fieldType = null;
		ParameterizedType parameterizedType = null;
		FieldIndexDetails fieldIndexDetails = null, idFieldDetails = null;
		
		//loop through the fields
		for(Field field : fields)
		{
			fieldType = field.getType();
			
			//for collection types get the collection type
			if(Collection.class.isAssignableFrom(fieldType))
			{
				parameterizedType = (ParameterizedType)field.getGenericType();
				fieldType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
			}
			
			indexField = field.getAnnotation(IndexField.class);
			
			//if field is not suppose to be indexed
			if(indexField == null)
			{
				continue;
			}
			
			//if the field type is not supported for indexing
			if(supportedTypes.containsKey(fieldType))
			{
				fieldIndexDetails = new FieldIndexDetails(field, supportedTypes.get(fieldType), indexField);
			}
			else if(Map.class.equals(fieldType))
			{
				fieldIndexDetails = new FieldIndexDetails(field, DataType.MAP, indexField);
			}
			else
			{
				fieldIndexDetails = new FieldIndexDetails(field, DataType.OBJECT, indexField);
				Map<String, FieldIndexDetails> subfields = new HashMap<>();
				
				fetchFields(fieldType, subfields);
				fieldIndexDetails.setSubfields(subfields);
			}
			
			fieldMap.put(field.getName(), fieldIndexDetails);
			
			if(fieldIndexDetails.isIdField())
			{
				if(idFieldDetails != null)
				{
					throw new InvalidConfigurationException("In index type '{}' Multiple fields are marked as id fields - [{}, {}]", 
							type.getName(), idFieldDetails.getName(), fieldIndexDetails.getName());
				}
			}
		}
	}
	
	public Collection<FieldIndexDetails> getFields()
	{
		return fields.values();
	}
	
	public FieldIndexDetails getField(String name)
	{
		FieldIndexDetails fieldDetails = fields.get(name);
		
		//if it is simple field field would be found directly
		if(fieldDetails != null)
		{
			return fieldDetails;
		}
		
		//if name does not refer to nested field
		if(!name.contains("."))
		{
			return null;
		}
		
		//divide the field path into parts and try to find the field
		String fieldParts[] = name.split("\\.");
		FieldIndexDetails parentFieldDetails = null, curFieldDetails = null;
		
		for(int i = 0; i < fieldParts.length; i++)
		{
			//for the first property
			if(parentFieldDetails == null)
			{
				parentFieldDetails = fields.get(fieldParts[i]);
				
				//if unable to identify primary part itself
				if(parentFieldDetails == null)
				{
					return null;
				}
				
				continue;
			}
			
			//if current field does not have any sub fields
			if(parentFieldDetails.getSubfields() == null)
			{
				//if map is encountered in the field path, return map field details
				if(DataType.MAP == parentFieldDetails.getDataType())
				{
					return parentFieldDetails;
				}
				
				return null;
			}
			
			curFieldDetails = parentFieldDetails.getSubfieldMap().get(fieldParts[i]);
			
			//if next field is not found in parent field
			if(curFieldDetails == null)
			{
				return null;
			}
			
			parentFieldDetails = curFieldDetails;
		}
		
		return curFieldDetails;
	}
	
	public Class<?> getType()
	{
		return type;
	}
}
