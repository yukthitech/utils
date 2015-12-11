/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.utils.beans;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.annotations.PropertyMapping;
import com.yukthi.utils.annotations.PropertyMappings;
import com.yukthi.utils.annotations.RecursiveAnnotationFactory;
import com.yukthi.utils.exceptions.InvalidConfigurationException;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Like Apache's {@link PropertyUtils} this class also maps properties from source bean to destination bean. With additional support of 
 * copying properties based on camel case property names. For example, relationId would be mapped to relation.id
 * @author akiran
 */
public class PropertyMapper
{
	private static Logger logger = LogManager.getLogger(PropertyMapper.class);

	private static RecursiveAnnotationFactory recursiveAnnotationFactory = new RecursiveAnnotationFactory();
	
	/**
	 * Cache map which caches the properties of different classes
	 */
	private static Map<Class<?>, BeanInfo> typeToProp = new HashMap<>();
	
	/**
	 * Validates the field information specified in the "mapping" and adds the mapping information
	 * to specified bean info
	 * @param beanInfo Bean info to which mapping needs to be added
	 * @param field Field on which mapping is found
	 * @param mapping Mapping to be added
	 */
	private static void addMapping(BeanInfo beanInfo, Field field, PropertyMapping mapping)
	{
		NestedProperty externalProperty = null, localProperty = null;
		
		try
		{
			externalProperty = NestedProperty.getNestedProperty(mapping.type(), mapping.from());
			
			String localPropertyName = field.getName();
			
			//if local sub property is specified
			if(mapping.subproperty().length() > 0)
			{
				localPropertyName = localPropertyName + "." + mapping.subproperty();
			}
			
			localProperty = NestedProperty.getNestedProperty(field.getDeclaringClass(), localPropertyName);
		}catch(Exception ex)
		{
			throw new InvalidConfigurationException(ex, "Invalid property mapping specified on field - {}.{}. "
					+ "An error occurred while processing mapping properties.", 
					field.getDeclaringClass().getName(), field.getName());
		}
		
		//ensure target and source are of same types
		if(!CommonUtils.isAssignable(externalProperty.getType(), localProperty.getType()))
		{
			throw new InvalidConfigurationException("Invalid property mapping specified on field - {}.{}. "
					+ "Source property type and target property type are not matching", 
					field.getDeclaringClass().getName(), field.getName());
		}
		
		beanInfo.addCustomMapping(mapping.type(), new MappingInfo(externalProperty, localProperty));
	}
	
	/**
	 * Fetches the mapping annotations from specified field and adds them to specified bean info
	 * @param beanInfo Bean info to which mappings needs to be added
	 * @param field Field from which mappings needs to be fetched
	 */
	private static void getMappingsFromField(BeanInfo beanInfo, Field field)
	{
		//check for direct mappings
		PropertyMapping directMapping = recursiveAnnotationFactory.findAnnotationRecursively(field, PropertyMapping.class); 
		
		if(directMapping != null)
		{
			addMapping(beanInfo, field, directMapping);
		}
		
		//check for group mappings, used to specify multiple mappings
		PropertyMappings propertyMappings = recursiveAnnotationFactory.findAnnotationRecursively(field, PropertyMappings.class);
		
		if(propertyMappings != null)
		{
			for(PropertyMapping mapping : propertyMappings.value())
			{
				addMapping(beanInfo, field, mapping);
			}
		}
	}
	
	/**
	 * Checks in the cache if the specified bean type property details is already loaded. If loaded returns the same. If not, builds the property map
	 * caches it and returns it.
	 * @param beanType Bean types for which property map needs to be fetched
	 * @return Property details of specified bean type
	 */
	public static synchronized BeanInfo getBeanInfo(Class<?> beanType)
	{
		BeanInfo beanInfo = typeToProp.get(beanType);
		
		//if type is already loaded return the same
		if(beanInfo != null)
		{
			return beanInfo;
		}
		
		beanInfo = new BeanInfo(beanType);
		Field fields[] = beanType.getDeclaredFields();
		PropertyInfo propInfo = null;
		
		//loop through property descriptors and add to bean property map
		for(Field field : fields)
		{
			propInfo = new PropertyInfo(field);
			beanInfo.addProperty(propInfo);

			getMappingsFromField(beanInfo, field);
		}
		
		//cache and return property map
		typeToProp.put(beanType, beanInfo);
		return beanInfo;
	}
	
	/**
	 * Called to check when property copy has to be done mismatching fields. Simple types like primitives, java core classes and arrays will be skipped.
	 * @param type Type to be checked
	 * @return
	 */
	private static boolean isIgnorableType(Class<?> type)
	{
		if(type.isPrimitive() || type.getName().startsWith("java") || type.isArray())
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Copies properties from "source" to "destination". This will be shallow copy. This copy also maps property camel case
	 * naming. For example, relationId would be mapped to relation.id
	 * @param destination Destination to which properties needs to be copied
	 * @param source Source from property values needs to be fetched
	 */
	public static void copyProperties(Object destination, Object source)
	{
		//ensure destination and source are provided
		if(destination == null)
		{
			throw new NullPointerException("Destination can not be null");
		}
		
		if(source == null)
		{
			throw new NullPointerException("Source can not be null");
		}
		
		//load source and destination property maps
		BeanInfo sourceBeanInfo = getBeanInfo(source.getClass());
		BeanInfo destinationBeanInfo = getBeanInfo(destination.getClass());
		Object value = null, destValue = null;
		
		PropertyInfo sourceProperty = null, destProperty = null;;
		
		//loop through source property and copy all simple (directly matching) properties
		for(String srcProp : sourceBeanInfo.getPropertyNames())
		{
			sourceProperty = sourceBeanInfo.getProperty(srcProp);
			destProperty = destinationBeanInfo.getProperty(srcProp);
			
			//if property is not found on destination ignore for now
			if(destProperty == null)
			{
				continue;
			}

			try
			{
				value = sourceProperty.getValue(source);
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while fetching property '{}' from source type '{}'", 
						srcProp, source.getClass().getName());
			}
			
			//if source value is not present
			if(value == null)
			{
				continue;
			}
			
			//if source and destination types are not matching throw error
			if(!CommonUtils.isAssignable(sourceProperty.getType(), destProperty.getType()))
			{
				if(isIgnorableType(sourceProperty.getType()) || isIgnorableType(destProperty.getType()))
				{
					logger.info("Ignoring property '{}' as source and destination data types are not matching "
							+ "[Source type : {}, Source Property Type: {}, Desctination Type: {}, Destination Property Type: {}] ", 
							srcProp, source.getClass().getName(), sourceProperty.getType().getName(), 
							destination.getClass().getName(), destProperty.getType().getName());
					continue;
				}
				
				try
				{
					destValue = destProperty.getValue(destination);
					
					if(destValue == null)
					{
						destValue = destProperty.getType().newInstance();
					}
					
					copyProperties(destValue, value);
					value = destValue;
				}catch(Exception ex)
				{
					logger.info("Ignoring mismatching property '{}' as source and destination data types are not matching and an error occurred while creating desination property bean "
							+ "[Source type : {}, Source Property Type: {}, Desctination Type: {}, Destination Property Type: {}]. Error - {}", 
							srcProp, source.getClass().getName(), sourceProperty.getType().getName(), 
							destination.getClass().getName(), destProperty.getType().getName(), ex);
					continue;
				}
			}
			
			//copy property from source to destination
			try
			{
				destProperty.setValue(destination, value);
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while setting property '{}' on destination type '{}'", 
						srcProp, destination.getClass().getName());
			}
		}
		
		List<MappingInfo> mappings = sourceBeanInfo.getMappings(destination.getClass()); 
		
		//loop through custom mappings from source
		if(mappings != null)
		{
			for(MappingInfo mapping : mappings)
			{
				value = mapping.getLocalProperty().getValue(source);
				
				if(value == null)
				{
					continue;
				}
				
				mapping.getExternalProperty().setValue(destination, value);
			}
		}
	
		//loop through custom mappings from destination
		mappings = destinationBeanInfo.getMappings(source.getClass());
		
		if(mappings != null)
		{
			for(MappingInfo mapping : mappings)
			{
				value = mapping.getExternalProperty().getValue(source);
				
				if(value == null)
				{
					continue;
				}
				
				mapping.getLocalProperty().setValue(destination, value);
			}
		}
	}
}
