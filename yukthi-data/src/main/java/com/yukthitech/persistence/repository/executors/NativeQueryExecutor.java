package com.yukthitech.persistence.repository.executors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.Record;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.repository.InvalidRepositoryException;
import com.yukthitech.persistence.repository.annotations.NativeQuery;
import com.yukthitech.persistence.repository.annotations.NativeQueryType;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.ReflectionUtils;
import com.yukthitech.utils.annotations.Named;
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
				
				Field fields[] = returnType.getDeclaredFields();
				
				for(Field field : fields)
				{
					returnTypeFields.put(field.getName().toLowerCase(), field);
				}
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
		
		Class<?> parameterTypes[] = method.getParameterTypes();

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
		
		for(String column : record.getColumnNames())
		{
			//flatten the column name
			flatColumnName = column.toLowerCase().replace("_", "");
			
			//get the result type field with same name
			field = returnTypeFields.get(flatColumnName);
			
			//if field is not found, ignore the column
			if(field == null)
			{
				continue;
			}
			
			//if return type is entity type fetch field details
			fieldDetails = returnEntityType != null ? returnEntityType.getFieldDetailsByField(field.getName()) : null;

			//if return type is expected to be entity and field details is found
			if(fieldDetails != null)
			{
				//set the value from record on field (after required conversion, if any)
				ReflectionUtils.setFieldValue(result, field.getName(), 
						conversionService.convertToJavaType(record.getObject(column), fieldDetails)
				);
			}
			//if return type is not entity type
			else
			{
				//set the value from record on field (after required conversion, if any)
				ReflectionUtils.setFieldValue(result, field.getName(), 
						ConvertUtils.convert(record.getObject(column), field.getType())
				);
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
				List<Record> records = dataStore.executeNativeFinder(nativeQueryAnnotation.name(), contextMap);

				//if single object is expected
				if(returnCollectionType == null)
				{
					if(CollectionUtils.isEmpty(records))
					{
						return null;
					}
					
					return parseToReturnType(records.get(0), dataStore);
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
				
				//convert found records into result objects
				if(records != null)
				{
					for(Record rec : records)
					{
						resCollection.add(parseToReturnType(rec, dataStore));
					}
				}
				
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
