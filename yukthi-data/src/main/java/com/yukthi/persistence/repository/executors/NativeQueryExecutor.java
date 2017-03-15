package com.yukthi.persistence.repository.executors;

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

import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.IDataStore;
import com.yukthi.persistence.Record;
import com.yukthi.persistence.conversion.ConversionService;
import com.yukthi.persistence.repository.InvalidRepositoryException;
import com.yukthi.persistence.repository.annotations.NativeQuery;
import com.yukthi.persistence.repository.annotations.NativeQueryType;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.ConvertUtils;
import com.yukthi.utils.ReflectionUtils;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Query executor for native queries
 * @author akiran
 */
@QueryExecutorPattern(annotatedWith = NativeQuery.class)
public class NativeQueryExecutor extends QueryExecutor
{
	private static Logger logger = LogManager.getLogger(NativeQueryExecutor.class);
	
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
	
	public NativeQueryExecutor(Class<?> repositoryType, Method method, EntityDetails entityDetails)
	{
		super.repositoryType = repositoryType;
		super.entityDetails = entityDetails;

		methodDesc = String.format("%s.%s()", repositoryType.getName(), method.getName());

		//fetch native query details
		nativeQueryAnnotation = method.getAnnotation(NativeQuery.class);
		returnType = method.getReturnType();
		
		//validate parameters
		Class<?> parameterTypes[] = method.getParameterTypes();
		
		if(parameterTypes != null && parameterTypes.length > 0)
		{
			if(parameterTypes.length > 1)
			{
				throw new InvalidRepositoryException("Multiple parameters specified for native query method - " + methodDesc);
			}
		}
		
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
	}
	
	/**
	 * Converts specified record into required result object
	 * @param record
	 * @return
	 */
	private Object parseToReturnType(Record record)
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

			//set the value from record on field (after required conversion, if any)
			ReflectionUtils.setFieldValue(result, field.getName(), 
					ConvertUtils.convert(record.getObject(column), field.getType())
			);
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
		
		Object context = (params.length > 0) ? params[0] : null;
		
		try
		{
			//if native is read method
			if(nativeQueryAnnotation.type() == NativeQueryType.READ)
			{
				List<Record> records = dataStore.executeNativeFinder(nativeQueryAnnotation.name(), context);

				//if single object is expected
				if(returnCollectionType == null)
				{
					if(CollectionUtils.isEmpty(records))
					{
						return null;
					}
					
					return parseToReturnType(records.get(0));
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
						resCollection.add(parseToReturnType(rec));
					}
				}
				
				return resCollection;
			}
			//if current query is DML query
			else
			{
				int res = dataStore.executeNativeDml(nativeQueryAnnotation.name(), context);
				
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
