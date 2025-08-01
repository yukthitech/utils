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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.ObjectWrapper;

/**
 * Service to convert object of one type into other.
 * @author akiran
 */
public class ConversionService
{
	private static Logger logger = LogManager.getLogger(ConversionService.class);
	
	private IImplicitCoverterProvider implicitCoverterProvider;
	
	private List<IPersistenceConverter> converters = new ArrayList<>();
	
	private Map<Class<?>, IPersistenceConverter> typeToConverter = new HashMap<>();
	
	public ConversionService(IImplicitCoverterProvider coverterProvider)
	{
		this.implicitCoverterProvider = coverterProvider;
		addConverter(new StringDbConverter());
		addConverter(new DateConverter());
	}
	
	/**
	 * Adds a converter that can modify data of one particular type to other
	 * @param converter
	 */
	public void addConverter(IPersistenceConverter converter)
	{
		if(converter == null)
		{
			throw new NullPointerException("Converter can not be null");
		}
		
		this.converters.add(converter);
	}
	
	private IPersistenceConverter getConverter(Field field, DataType dbDataType, Class<?> valueType, ObjectWrapper<DataType> finalDbDataType)
	{
		DataTypeMapping typeMapping = field.getAnnotation(DataTypeMapping.class);
		
		if(typeMapping == null)
		{
			if(valueType != null)
			{
				dbDataType = (dbDataType == null) ? DataType.getDataType(valueType) : dbDataType;
				finalDbDataType.setValue(dbDataType);
			}
			
			return implicitCoverterProvider.getImplicitConverter(dbDataType);
		}
		
		if(valueType != null)
		{
			dbDataType = (dbDataType == null) ? typeMapping.type() : dbDataType;
			dbDataType = (dbDataType == DataType.UNKNOWN) ? DataType.getDataType(valueType) : dbDataType;
			finalDbDataType.setValue(dbDataType);
		}
		
		Class<?> converterType = typeMapping.converterType();
		
		//if no converter is specified in annotation
		if(IPersistenceConverter.class.equals(converterType))
		{
			// use implicit converter, if any
			return implicitCoverterProvider.getImplicitConverter(dbDataType);
		}
		
		IPersistenceConverter converter = typeToConverter.get(converterType);
		
		if(converter != null)
		{
			return converter;
		}
		
		try
		{
			converter = (IPersistenceConverter)converterType.getConstructor().newInstance();
		}catch(Exception ex)
		{
			throw new IllegalStateException("Failed to create converter of type: " + converterType.getName(), ex);
		}
		
		typeToConverter.put(converterType, converter);
		return converter;
	}

	/**
	 * Fetches the converter for specified field, if it is explcitly defined on 
	 * target java field
	 * @param fieldDetails
	 * @return
	 */
	private IPersistenceConverter getConverter(FieldDetails fieldDetails)
	{
		//TODO: Check why field details needs to be null. Is there any substitute.
		if(fieldDetails == null || fieldDetails.getField() == null)
		{
			return null;
		}
		
		return getConverter(fieldDetails.getField(), fieldDetails.getDbDataType(), null, null);
	}
	
	/**
	 * Converts specified db object to matching java type
	 * @param dbObject
	 * @param fieldDetails
	 * @return
	 */
	public Object convertToJavaType(Object dbObject, FieldDetails fieldDetails)
	{
		//when db object is null, return null
		if(dbObject == null)
		{
			return null;
		}
		
		//fetch field specific converter
		IPersistenceConverter converter = getConverter(fieldDetails);
		
		//if field specific converter is present
		if(converter != null)
		{
			return converter.convertToJavaType(dbObject, fieldDetails.getDbDataType(), 
					fieldDetails.getField().getType(), fieldDetails.getField());
		}
		
		//try to convert using default converters and in generic way
		return convert(dbObject, fieldDetails.getDbDataType(), 
				fieldDetails.getField().getType(), fieldDetails.getField());
	}
	
	public Object convertToJavaType(Object dbObject, Field field)
	{
		//when db object is null, return null
		if(dbObject == null)
		{
			return null;
		}
		
		//fetch field specific converter
		ObjectWrapper<DataType> dbDataType = new ObjectWrapper<>();
		IPersistenceConverter converter = getConverter(field, null, dbObject.getClass(), dbDataType);
		
		//if field specific converter is present
		if(converter != null)
		{
			return converter.convertToJavaType(dbObject, dbDataType.getValue(), 
					field.getType(), field);
		}
		
		//try to convert using default converters and in generic way
		return convert(dbObject, dbDataType.getValue(), field.getType(), field);
	}

	/**
	 * Converts specified java object into target db type
	 * @param javaObj
	 * @param fieldDetails
	 * @return
	 */
	public Object convertToDBType(Object javaObj, FieldDetails fieldDetails)
	{
		//when java object is null, return null
		if(javaObj == null)
		{
			return null;
		}
		
		//fieldDetails will be null, when conversion is needed for values in conditions
		if(fieldDetails == null)
		{
			return javaObj;
		}
		
		//fetch field specific converter
		IPersistenceConverter fldConverter = getConverter(fieldDetails);
		
		//if field specific converter is present
		if(fldConverter != null)
		{
			return fldConverter.convertToDBType(javaObj, fieldDetails.getDbDataType());
		}
		
		Object result = null;
		
		//check if any converter can handle conversion
		for(IPersistenceConverter converter: converters)
		{
			result = converter.convertToDBType(javaObj, fieldDetails.getDbDataType());

			//if conversion was successful
			if(result != null)
			{
				return result;
			}
		}
		
		//if no converter is able to convert, simply return actual value
		return javaObj;
	}
	
	/**
	 * Converts specified db object into specified java type using default converters 
	 * @param dbObject
	 * @param dbDataType
	 * @param targetType
	 * @return
	 */
	private Object convert(Object dbObject, DataType dbDataType, Class<?> targetType, Field field)
	{
		//if from value is null
		if(dbObject == null)
		{
			return null;
		}
		
		//if db object is same as target java type
		if(targetType.isAssignableFrom(dbObject.getClass()))
		{
			return dbObject;
		}
		
		//if db value is string and its empty
		if((dbObject instanceof String) && ((String)dbObject).trim().length() == 0)
		{
			return null;
		}
		
		Object result = null;
		
		//check if any of the default converters can convert current db object
		for(IPersistenceConverter converter: converters)
		{
			result = converter.convertToJavaType(dbObject, dbDataType, 
					targetType, field);
			
			//if conversion was success
			if(result != null)
			{
				return result;
			}
		}

		//if in built converters are not able to convert, use generic utils to convert
		try
		{
			return ConvertUtils.convert(dbObject, targetType);
		}catch(Exception ex)
		{
			logger.warn("An error occurred while converting '{}' to type '{}' in generic way. Error - {}", dbObject, targetType.getName(), ex);
		}
		
		throw new DataConversionException("Failed to convert to '" + targetType.getName() + "' from value - " + dbObject + "[" + dbObject.getClass().getName() + "]");
	}
}
