package com.yukthi.persistence.repository.executors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.ccg.util.CCGUtility;
import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.IDataStore;
import com.yukthi.persistence.Record;
import com.yukthi.persistence.RecordCountMistmatchException;
import com.yukthi.persistence.conversion.ConversionService;
import com.yukthi.persistence.query.FinderQuery;
import com.yukthi.persistence.repository.InvalidRepositoryException;
import com.yukthi.persistence.repository.annotations.NativeQuery;
import com.yukthi.persistence.repository.annotations.NativeQueryType;

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
			
			//load the return type fields
			returnTypeFields = new HashMap<>();
			Field fields[] = returnType.getDeclaredFields();
			
			for(Field field : fields)
			{
				returnTypeFields.put(field.getName().toLowerCase(), field);
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
	}

	@Override
	public Object execute(IDataStore dataStore, ConversionService conversionService, Object... params)
	{
		logger.trace("Started method: execute");
		
		queryLock.lock();
		
		Object context = (params.length > 0) ? params[0] : null;
		
		try
		{
			if(nativeQueryAnnotation.type() == NativeQueryType.READ)
			{
			}
			else
			{
				int res = dataStore.executeNativeDml(nativeQueryAnnotation.name(), context);
			}
			
			return null;
		}finally
		{
			queryLock.unlock();
		}
	}
	
	
}
