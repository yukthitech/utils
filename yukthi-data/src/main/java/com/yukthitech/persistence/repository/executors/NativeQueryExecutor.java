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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.FilterAction;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.IDataFilter;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.IFinderRecordProcessor;
import com.yukthitech.persistence.Record;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.repository.InvalidRepositoryException;
import com.yukthitech.persistence.repository.annotations.NativeQuery;
import com.yukthitech.persistence.repository.annotations.NativeQueryType;
import com.yukthitech.persistence.repository.executors.proxy.ProxyEntityCreator;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.ReflectionUtils;
import com.yukthitech.utils.annotations.Named;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Query executor for native queries
 * @author akiran
 */
@QueryExecutorPattern(annotatedWith = NativeQuery.class)
public class NativeQueryExecutor extends QueryExecutor
{
	private static Logger logger = LogManager.getLogger(NativeQueryExecutor.class);
	
	/**
	 * Name to be used for single parameter methods when no explicit name is provided.
	 */
	public static final String SINGLE_PARAM_DEF_NAME = "query";
	
	/**
	 * Context parameter name for accessing parameters array of the method.
	 */
	public static final String PARAMS_NAME = "params";
	
	/**
	 * Lock for query execution
	 */
	private ReentrantLock queryLock = new ReentrantLock();
	
	/**
	 * Description of the target repository method
	 */
	private String methodDesc = null;
	
	/**
	 * Return type of repository method. For finder method, if return type is collection, this would be collection parameter type
	 */
	private Class<?> returnType = null;
	
	/**
	 * For finder method, if return type is collection, this would collection type. Otherwise null
	 */
	private Class<?> returnCollectionType = null;
	
	/**
	 * For finder methods, name to field mapping. Name would be flatten version (lower case of field name)
	 */
	private Map<String, Field> returnTypeFields;
	
	private NativeQuery nativeQueryAnnotation;
	
	private Method postConstructMethod;
	
	/**
	 * Mapping from parameter name to index of parameter. Which would be used in constructing native queries context.
	 */
	private Map<String, Integer> paramNameToIdx = new HashMap<>();
	
	/**
	 * Parameter index at which data filter can be expected.
	 */
	private int dataFilterIndex = -1;

	public NativeQueryExecutor(Class<?> repositoryType, Method method, EntityDetails entityDetails)
	{
		super.repositoryType = repositoryType;
		super.entityDetails = entityDetails;

		methodDesc = String.format("%s.%s()", repositoryType.getName(), method.getName());

		//fetch native query details
		nativeQueryAnnotation = method.getAnnotation(NativeQuery.class);
		returnType = method.getReturnType();
		
		//for read methods, load return type field details and set the collection type as needed.
		if(nativeQueryAnnotation.type() == NativeQueryType.READ)
		{
			//set the collection type if required
			if(Collection.class.isAssignableFrom(returnType))
			{
				if(returnType.isAssignableFrom(ArrayList.class))
				{
					this.returnCollectionType = ArrayList.class;
				}
				else if(returnType.isAssignableFrom(HashSet.class))
				{
					this.returnCollectionType = HashSet.class;
				}
				else
				{
					try
					{
						returnType.newInstance();
						this.returnCollectionType = returnType;
					}catch(Exception ex)
					{
						throw new InvalidRepositoryException("Unsupported collection return type found native finder - " + methodDesc);
					}
				}

				returnType = (Class<?>)((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
			}
			
			//load the return type fields for non primitive types
			if(!returnType.isPrimitive() && !CommonUtils.isWrapperClass(returnType))
			{
				returnTypeFields = new HashMap<>();
				fetchReturnFieldDetails(returnType);
			}
		}
		else
		{
			if(!void.class.equals(returnType) && 
				!boolean.class.equals(returnType) &&
				!int.class.equals(returnType))
			{
				throw new InvalidRepositoryException("Native DML method '" + method.getName() + "' found with non-boolean, non-void and non-int return type in repository: " + repositoryType.getName());
			}
		}
		
		Class<?> parameterTypes[] = null;

		//find post construct method
		for(Method rmethod : returnType.getDeclaredMethods())
		{
			//if method does not have @PostConstruct method, ignore the method
			if(rmethod.getAnnotation(PostConstruct.class) == null)
			{
				continue;
			}
			
			//if method parameters, ignore the method
			parameterTypes = rmethod.getParameterTypes();
			
			if(parameterTypes != null && parameterTypes.length > 0)
			{
				continue;
			}
			
			this.postConstructMethod = rmethod;
			method.setAccessible(true);
			break;
		}
		
		//fetch the parameter name indexing
		createParamNameMap(method);

		if(returnCollectionType != null)
		{
			checkForDataFilter(method);
		}
	}
	
	/**
	 * Fetches return fields from specified type.
	 * @param type type from which fields needs to be fetched.
	 */
	private void fetchReturnFieldDetails(Class<?> type)
	{
		if(type.getName().startsWith("java"))
		{
			return;
		}
		
		Field fields[] = type.getDeclaredFields();
		
		for(Field field : fields)
		{
			returnTypeFields.put(field.getName().toLowerCase(), field);
		}
		
		fetchReturnFieldDetails(type.getSuperclass());
	}
	
	/**
	 * Checks if data filter is specified in method arguments, if specified sets {@link #dataFilterIndex} with the parameter index
	 * of the data filter.
	 * @param method mehod whose paramters needs to be searched for filter
	 */
	private void checkForDataFilter(Method method)
	{
		Type paramTypes[] = method.getGenericParameterTypes();
		ParameterizedType parameterizedType = null;
		
		for(int i = 0; i < paramTypes.length; i++)
		{
			if(!(paramTypes[i] instanceof ParameterizedType))
			{
				continue;
			}
			
			parameterizedType = (ParameterizedType) paramTypes[i];
			
			if(! IDataFilter.class.isAssignableFrom((Class<?>)parameterizedType.getRawType()) )
			{
				continue;
			}
			
			if(!TypeUtils.isAssignable(parameterizedType.getActualTypeArguments()[0], returnType))
			{
				throw new InvalidRepositoryException("Data-filter argument type '{}' is not matching with finder return type '{}'. [Method: {}, Repository: {}]",
						parameterizedType.getActualTypeArguments()[0].toString(), returnType.getName(), method.getName(), repositoryType.getName());
			}
			
			this.dataFilterIndex = i;
			break;
		}
	}
	
	/**
	 * Iterates through method parameter annotations. If any parameter is named explicitly then it
	 * is loaded into {@link #paramNameToIdx} map.
	 * @param method
	 */
	private void createParamNameMap(Method method)
	{
		Annotation paramAnnotaions[][] = method.getParameterAnnotations();
		Named named = null;
		
		//loop through method param annotations
		for(int i = 0; i < paramAnnotaions.length; i++)
		{
			//loop through current param annotation
			for(int j = 0; j < paramAnnotaions[i].length; j++)
			{
				//if current annotation is named annotation
				if(paramAnnotaions[i][j] instanceof Named)
				{
					named = (Named) paramAnnotaions[i][j];
					this.paramNameToIdx.put(named.value(), i);
				}
			}
		}
		
		//if no explicit names are provided and method has single parameter
		//	then by default name that single param as query
		if(this.paramNameToIdx.isEmpty() && method.getParameterTypes().length == 1)
		{
			this.paramNameToIdx.put(SINGLE_PARAM_DEF_NAME, 0);
		}
	}
	
	/**
	 * Converts specified record into required result object
	 * @param record
	 * @return
	 */
	private Object parseToReturnType(Record record, IDataStore dataStore)
	{
		if(returnTypeFields == null)
		{
			return ConvertUtils.convert(record.getObject(0), returnType);
		}
		
		Object result = null;
		
		//create result object instance
		try
		{
			result = returnType.newInstance();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating result object of type - {}", returnType.getName());
		}
		
		//copy values from record to result object fields
		Field field = null;
		String flatColumnName = null;
		EntityDetails returnEntityType = dataStore.getEntityDetails(returnType);
		ConversionService conversionService = dataStore.getConversionService();
		FieldDetails fieldDetails = null;
		
		Map<String, Object> flatColumnMap = new HashMap<>();
		
		for(String column : record.getColumnNames())
		{
			//flatten the column name
			flatColumnName = column.toLowerCase().replace("_", "");
			
			flatColumnMap.put(flatColumnName, record.getObject(column));
			
			//get the result type field with same name
			field = returnTypeFields.get(flatColumnName);
			
			//if return type is entity type fetch field details
			if(returnEntityType != null)
			{
				fieldDetails = field != null ? returnEntityType.getFieldDetailsByField(field.getName()) : null;
				
				//if field details cannot be found by field name, try to find by column name
				if(fieldDetails == null)
				{
					fieldDetails = returnEntityType.getFieldDetailsByColumn(column);
				}
			}
			//if return type is not entity type
			else
			{
				fieldDetails = null;
			}

			//if return type is expected to be entity and field details is found
			if(fieldDetails != null)
			{
				//if no matching field is found, get the field from details
				if(field == null)
				{
					field = fieldDetails.getField();
				}
				
				//set the value from record on field (after required conversion, if any)
				ReflectionUtils.setFieldValue(result, field, 
						conversionService.convertToJavaType(record.getObject(column), fieldDetails)
				);
			}
			//if return type is not entity type
			else if(field != null)
			{
				//set the value from record on field (after required conversion, if any)
				ReflectionUtils.setFieldValue(result, field, 
						ConvertUtils.convert(record.getObject(column), field.getType())
				);
			}
			else
			{
				throw new InvalidConfigurationException("No field is found matching with column '{}' for result bean: {}", column, returnType.getName());
			}
		}
		
		if(postConstructMethod != null)
		{
			try
			{
				postConstructMethod.invoke(result);
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while invoking post construct method - {}.{}", returnType.getName(), postConstructMethod.getName());
			}
		}
		
		boolean isEntityResultType = (returnType.getAnnotation(Table.class) != null);
		
		//if return type is target entity type, wrap result with proxy to take care of sub relations
		if(isEntityResultType)
		{
			ICrudRepository<?> resultRepo = super.getCrudRepository(returnType); 
			result = ProxyEntityCreator.newProxyByEntity(resultRepo.getEntityDetails(), resultRepo, result, flatColumnMap);
		}
		
		return result;
	}

	/**
	 * Executes the target query
	 * @param dataStore
	 * @param conversionService
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object execute(QueryExecutionContext exeContext, IDataStore dataStore, ConversionService conversionService, Object... params)
	{
		logger.trace("Started method: execute");
		
		queryLock.lock();
		
		//create context map for parsing native queries
		Map<String, Object> contextMap = new HashMap<>();
		
		//add method parameters array on context
		contextMap.put(PARAMS_NAME, params);
		
		//set parameters on context which are named explicitly using @Named annotation
		Integer index = null;
		
		for(String paramName : this.paramNameToIdx.keySet())
		{
			index = this.paramNameToIdx.get(paramName);
			contextMap.put(paramName, params[index]);
		}
		
		try
		{
			//if native is read method
			if(nativeQueryAnnotation.type() == NativeQueryType.READ)
			{
				//list that will maintain final result beans
				final ArrayList<Object> resLst = new ArrayList<>();

				//identify the data filter
				final IDataFilter<Object> dataFilter = (dataFilterIndex < 0) ? null : (IDataFilter<Object>) params[dataFilterIndex];

				dataStore.executeNativeFinder(nativeQueryAnnotation.name(), contextMap, new IFinderRecordProcessor()
				{
					@Override
					public Action process(long recordNo, Record record)
					{
						if(returnCollectionType == null && recordNo > 2)
						{
							return Action.STOP;
						}
						
						Object recordBean = parseToReturnType(record, dataStore);
						
						if(dataFilter != null)
						{
							FilterAction filterAction = dataFilter.filter(recordBean);
							filterAction = (filterAction == null) ? FilterAction.ACCEPT : filterAction;
							
							if(filterAction.isDataAccepted())
							{
								resLst.add(recordBean);
							}
							
							return filterAction.isStopProcessing() ? Action.STOP : Action.IGNORE; 
						}
						
						resLst.add( recordBean );
						return Action.IGNORE;
					}
				});

				//if single object is expected
				if(returnCollectionType == null)
				{
					if(CollectionUtils.isEmpty(resLst))
					{
						return null;
					}
					
					return resLst.get(0);
				}

				//create result collection type
				Collection<Object> resCollection = null;
				
				try
				{
					resCollection = (Collection)returnCollectionType.newInstance();
				}catch(Exception ex)
				{
					throw new InvalidStateException(ex, "An error occurred while creating collection instance - {}", returnCollectionType.getName());
				}
				
				resCollection.addAll(resLst);
				
				return resCollection;
			}
			//if current query is DML query
			else
			{
				int res = dataStore.executeNativeDml(nativeQueryAnnotation.name(), contextMap);
				
				if(boolean.class.equals(returnType))
				{
					return (res > 0);
				}
				
				return res;
			}
		}finally
		{
			queryLock.unlock();
		}
	}
}
