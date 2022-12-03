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
package com.yukthitech.persistence.repository.executors;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.InvalidMappingException;
import com.yukthitech.persistence.repository.InvalidRepositoryException;
import com.yukthitech.persistence.repository.annotations.ExtendedFieldNames;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.FirstRowOnly;
import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.persistence.repository.annotations.OrderByField;
import com.yukthitech.persistence.repository.annotations.OrderByType;
import com.yukthitech.persistence.repository.annotations.ResultMapping;
import com.yukthitech.persistence.repository.annotations.SearchResult;
import com.yukthitech.persistence.repository.executors.builder.ConditionQueryBuilder;
import com.yukthitech.persistence.repository.search.IDynamicSearchResult;
import com.yukthitech.utils.annotations.RecursiveAnnotationFactory;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;

/**
 * Provides common base functionality for search query type executors - Finder and Search queries
 * @author akiran
 */
public abstract class AbstractSearchQuery extends QueryExecutor
{
	private static Logger logger = LogManager.getLogger(AbstractSearchQuery.class);
	
	private static RecursiveAnnotationFactory recursiveAnnotationFactory = new RecursiveAnnotationFactory();
	
	protected Class<?> returnType;
	
	protected Type genericReturnType;
	
	protected Class<?> collectionReturnType = null;
	
	/**
	 * Flag indicating only first row only should be fetched.
	 */
	protected boolean firstRowOnly = false;

	/**
	 * Keeps track of different parts required by query
	 */
	protected ConditionQueryBuilder conditionQueryBuilder;
	
	/**
	 * Description of the method
	 */
	protected String methodDesc;
	
	/**
	 * Fetches result field from return object type
	 * @param returnType
	 * @param query
	 * @param index
	 */
	private void fetchResultFieldsFromObject(Class<?> returnType)
	{
		Class<?> curCls = returnType;
		
		while(true)
		{
			if(curCls.getName().startsWith("java"))
			{
				break;
			}
			
			java.lang.reflect.Field fields[] = curCls.getDeclaredFields();
			Field resultField = null;
			String name = null;
			
			//loop through query object type fields 
			for(java.lang.reflect.Field objField : fields)
			{
				resultField = objField.getAnnotation(Field.class);
				
				//if field is not marked as condition
				if(resultField == null)
				{
					continue;
				}
				
				//fetch entity field name
				name = resultField.value();
				
				//if name is not specified in condition
				if(name.trim().length() == 0)
				{
					//use field name
					name = objField.getName();
				}
				
				conditionQueryBuilder.addResultField(objField.getName(), objField.getType(), objField.getGenericType(), name, methodDesc);
			}
			
			curCls = curCls.getSuperclass();
		}
	}
	
	/**
	 * Fetches/populates entity fields as result fields
	 */
	protected void fetchEntityResultFields()
	{
		logger.trace("Started method: setFullEntityDetails");
		
		//loop through entity details
		for(FieldDetails field: entityDetails.getFieldDetails())
		{
			//if the field is not owned by this table
			if(!field.isTableOwned())
			{
				if(field.isTableJoined())
				{
					
				}
				
				continue;
			}

			//adds the current field as result field
			conditionQueryBuilder.addResultField(field.getName(), field.getField().getType(), field.getField().getGenericType(), field.getName(), methodDesc);
		}
		
		this.returnType = entityDetails.getEntityType();
		this.genericReturnType = this.returnType;
	}
	
	/**
	 * Fetches the concrete collection type based on specified return type.
	 * @param returnType Return collection type
	 * @param methodName method name to be used in exception messages, if any
	 * @return concrete matching collection type
	 */
	protected Class<?> getCollectionType(Class<?> returnType, String methodName)
	{
		if(returnType.isAssignableFrom(ArrayList.class))
		{
			return ArrayList.class;
		}
		else if(returnType.isAssignableFrom(HashSet.class))
		{
			return HashSet.class;
		}
		else if(returnType.isAssignableFrom(TreeSet.class))
		{
			return TreeSet.class;
		}

		try
		{
			returnType.newInstance();
			return returnType;
		}catch(Exception ex)
		{
			throw new InvalidRepositoryException("Unsupported collection return type found on finder '" 
						+ methodName + "' of repository: " + repositoryType.getName());
		}
	}
	
	protected void fetchReturnDetails(Method method)
	{
		logger.trace("Started method: fetchReturnDetails");
		
		this.returnType = method.getReturnType();
		this.genericReturnType = method.getGenericReturnType();
		
		if(void.class.equals(this.returnType))
		{
			throw new InvalidRepositoryException("Found void return type, when return type is expected. [Repository: {}, Method: {}]", repositoryType.getName(), method.getName());
		}
		
		//TODO: Support map types
		if(Collection.class.isAssignableFrom(returnType))
		{
			this.collectionReturnType = getCollectionType(returnType, method.getName());
			
			ParameterizedType type = (ParameterizedType)method.getGenericReturnType();
			Type typeArgs[] = type.getActualTypeArguments();
			
			if(typeArgs.length != 1)
			{
				throw new InvalidRepositoryException("Unsupported collection return type (with mutliple type params) found on finder '" 
							+ method.getName() + "' of repository: " + repositoryType.getName());
			}

			this.returnType = TypeUtils.getRawType(typeArgs[0], repositoryType);
		}
		else
		{
			this.returnType = TypeUtils.getRawType(method.getGenericReturnType(), repositoryType);
		}
		
		SearchResult searchResult = recursiveAnnotationFactory.findAnnotationRecursively(method, SearchResult.class);
		FirstRowOnly firstRowOnlyAnnnot = recursiveAnnotationFactory.findAnnotationRecursively(method, FirstRowOnly.class);
		
		this.firstRowOnly = (firstRowOnlyAnnnot != null);
		
		//if return type matches with entity type, add all entity fields as result fields
		if(entityDetails.getEntityType().equals(this.returnType) || ICrudRepository.class.equals(method.getDeclaringClass()))
		{
			fetchEntityResultFields();
		}
		//if method is annotated with Field annotation use that only as return field
		else if(method.getAnnotation(Field.class) != null)
		{
			Field field = method.getAnnotation(Field.class);
			FieldDetails fieldDetails = entityDetails.getFieldDetailsByField(field.value());
			
			//In case of converted field type (json fields, etc), if return type matches with field type,
			// 	then actual return type should be used (even in case of collection return type), not collection element type
			if(fieldDetails != null && !fieldDetails.isRelationField() && 
				fieldDetails.getField().getGenericType().equals(method.getGenericReturnType()))
			{
				this.collectionReturnType = null;
				this.returnType = method.getReturnType();
			}
			
			conditionQueryBuilder.addResultField(null, this.returnType, this.genericReturnType, field.value(), methodDesc);
		}
		else if(searchResult != null)
		{
			ResultMapping mappings[] = searchResult.mappings();

			//if mappings are specified fetch field details from bean fields
			if(mappings == null || mappings.length == 0)
			{
				fetchResultFieldsFromObject(returnType);
			}
			//if mappings are specified, add specified mappings to query-builder
			else
			{
				try
				{
					PropertyDescriptor propertyDescriptor = null;
					Object returnSampleBean = this.returnType.newInstance();
					
					for(ResultMapping mapping : mappings)
					{
						propertyDescriptor = PropertyUtils.getPropertyDescriptor(returnSampleBean, mapping.property());
						conditionQueryBuilder.addResultField(mapping.property(), propertyDescriptor.getPropertyType(), propertyDescriptor.getReadMethod().getGenericReturnType(), mapping.entityField(), methodDesc);
					}
				}catch(Exception ex)
				{
					throw new InvalidMappingException("An error occurred while parsing @SearchResult mappings of " + methodDesc, ex);
				}
			}
		}
		else
		{
			throw new UnsupportedOperationException("Failed to determine return details of finder method: " + methodDesc);
		}
	}
	
	/**
	 * Fetches order by fields for the specified method.
	 * @param method Method from which order by details needs to be fetched.
	 */
	protected void fetchOrderDetails(Method method)
	{
		OrderBy orderBy = recursiveAnnotationFactory.findAnnotationRecursively(method, OrderBy.class);
		
		if(orderBy == null)
		{
			return;
		}
		
		if(orderBy.fields().length > 0)
		{
			for(OrderByField field : orderBy.fields())
			{
				if(!entityDetails.hasField(field.name()))
				{
					throw new InvalidMappingException("Invalid field '" + field.name() + "' specified in @OrderBy annotation of finder method - " + methodDesc);
				}
				
				conditionQueryBuilder.addOrderByField(field.name(), field.type(), methodDesc);
			}
		}
		else
		{
			for(String field : orderBy.value())
			{
				if(!entityDetails.hasField(field))
				{
					throw new InvalidMappingException("Invalid field '" + field + "' specified in @OrderBy annotation of finder method - " + methodDesc);
				}
				
				conditionQueryBuilder.addOrderByField(field, OrderByType.ASC, methodDesc);
			}
		}
	}
	
	/**
	 * Fetches param index which provides custom fields to fetch.
	 * @param method Method from which param needs to be fetched.
	 * @return index at which param names might have specified.
	 */
	protected int getExtendedFieldParam(Method method)
	{
		//if extended table is not present return
		if(entityDetails.getExtendedTableDetails() == null)
		{
			return -1;
		}
		
		//if return type is not entity type and not dynamic search result, ignore extension field names, if any
		if(!returnType.equals(entityDetails.getEntityType()) && !IDynamicSearchResult.class.isAssignableFrom(returnType))
		{
			return -1;
		}
		
		Parameter params[] = method.getParameters();
		
		if(params == null || params.length == 0)
		{
			return -1;
		}
		
		ParameterizedType parameterizedType = null;
		
		for(int i = 0; i < params.length; i++)
		{
			if(params[i].getAnnotation(ExtendedFieldNames.class) != null)
			{
				if(!Collection.class.isAssignableFrom(params[i].getType()))
				{
					throw new InvalidConfigurationException("Non collection param at index {} is declared as extended-field-names - {}", i, methodDesc);
				}
				
				parameterizedType = (ParameterizedType) params[i].getParameterizedType();
				
				if(!String.class.equals(parameterizedType.getActualTypeArguments()[0]))
				{
					throw new InvalidConfigurationException("Non String collection param at index {} is declared as extended-field-names - {}", i, methodDesc);
				}
				
				return i;
			}
		}
		
		return -1;
	}

	/**
	 * Gets the collection return type.
	 *
	 * @return the collection return type
	 */
	public Class<?> getCollectionReturnType()
	{
		return collectionReturnType;
	}
}
