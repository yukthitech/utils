package com.yukthitech.persistence.repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.ForeignConstraintDetails;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.JoinTableDetails;
import com.yukthitech.persistence.TransactionException;
import com.yukthitech.persistence.query.DropTableQuery;
import com.yukthitech.persistence.repository.annotations.NotExecutableMethod;
import com.yukthitech.persistence.repository.executors.QueryExecutionContext;
import com.yukthitech.persistence.repository.executors.QueryExecutor;
import com.yukthitech.utils.annotations.RecursiveAnnotationFactory;
import com.yukthitech.utils.exceptions.UnsupportedOperationException;

class RepositoryProxy implements InvocationHandler
{
	private static Logger logger = LogManager.getLogger(RepositoryProxy.class);

	private static RecursiveAnnotationFactory recursiveAnnotationFactory = new RecursiveAnnotationFactory();
	
	private IDataStore dataStore;
	private Map<String, QueryExecutor> methodToExecutor = new HashMap<>();
	private EntityDetails entityDetails;

	private Map<String, Function<Object[], Object>> defaultedMethods = new HashMap<>();
	private Class<? extends ICrudRepository<?>> repositoryType;
	
	private Set<Method> nonExecutableMethods = new HashSet<>();
	
	private QueryExecutionContext queryExecutionContext = new QueryExecutionContext();
	
	public RepositoryProxy(IDataStore dataStore, Class<? extends ICrudRepository<?>> repositoryType, EntityDetails entityDetails, ExecutorFactory executorFactory)
	{
		defaultedMethods.put("getEntityDetails", this::getEntityDetails);
		defaultedMethods.put("newTransaction", this::newTransaction);
		defaultedMethods.put("currentTransaction", this::currentTransaction);
		defaultedMethods.put("newOrExistingTransaction", this::newOrExistingTransaction);
		defaultedMethods.put("dropEntityTable", this::dropEntityTable);
		defaultedMethods.put("getRepositoryType", this::getRepositoryType);
		defaultedMethods.put("setExecutionContext", this::setExecutionContext);

		this.dataStore = dataStore;
		this.entityDetails = entityDetails;
		this.repositoryType = repositoryType;
		
		Method methods[] = repositoryType.getMethods();
		String methodName = null;
		QueryExecutor queryExecutor = null;
		
		for(Method method: methods)
		{
			methodName = method.getName();
			
			if(defaultedMethods.containsKey(methodName))
			{
				continue;
			}
			
			if(methodToExecutor.containsKey(method.getName()))
			{
				throw new InvalidRepositoryException("Duplicate method '" + method.getName() + "' encouneted in repository: " + repositoryType.getName());
			}
			
			if(recursiveAnnotationFactory.findAnnotationRecursively(method, NotExecutableMethod.class) != null)
			{
				nonExecutableMethods.add(method);
				continue;
			}

			queryExecutor = executorFactory.getQueryExecutor(repositoryType, method, entityDetails);
			
			if(queryExecutor != null)
			{
				methodToExecutor.put(methodName, queryExecutor);
				continue;
			}
			
			throw new InvalidRepositoryException("Invalid CRUD method '" + methodName + "' is specified for entity - " + entityDetails.getEntityType().getName());
		}
		
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		//if current method is non-executable then throw exception
		if(nonExecutableMethods.contains(method))
		{
			throw new UnsupportedOperationException("Repository method {}.{}() is marked with @{}. This method can not be executed directly.", 
					repositoryType.getName(), method.getName(), NotExecutableMethod.class.getName());
		}
		
		String methodName = method.getName();
				
		if(defaultedMethods.containsKey(methodName))
		{
			logger.debug("Executing default-method '" + method.getName() + "' with arguments: " + Arrays.toString(args));
			
			return defaultedMethods.get(methodName).apply(args);
		}
		
		logger.debug("Executing method '" + method.getName() + "' with arguments: " + Arrays.toString(args));
		
		try
		{
			QueryExecutor queryExecutor = methodToExecutor.get(method.getName());
			return queryExecutor.execute(queryExecutionContext, dataStore, dataStore.getConversionService(), args);
		}catch(RuntimeException ex)
		{
			logger.debug("An error occurred while executing method: " + method.getName() + "\nError: " + ex);
			throw ex;
		}
	}

	private Object getEntityDetails(Object args[])
	{
		return entityDetails;
	}
	
	private Object newTransaction(Object args[])
	{
		try
		{
			return dataStore.getTransactionManager().newTransaction();
		}catch(TransactionException e)
		{
			throw new IllegalStateException(e);
		}
	}
	
	private Object currentTransaction(Object args[])
	{
		try
		{
			return dataStore.getTransactionManager().currentTransaction();
		}catch(TransactionException e)
		{
			throw new IllegalStateException(e);
		}
	}
	
	private Object newOrExistingTransaction(Object args[])
	{
		try
		{
			return dataStore.getTransactionManager().newOrExistingTransaction();
		}catch(TransactionException e)
		{
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Drops current entity table
	 * @param args
	 * @return
	 */
	private Object dropEntityTable(Object args[])
	{
		//if foreign keys are present
		if(entityDetails.getForeignConstraints() != null)
		{
			JoinTableDetails joinTableDetails = null;
					
			//loop through foreign constraints and check for join tables
			for(ForeignConstraintDetails constraintDetails : entityDetails.getForeignConstraints())
			{
				joinTableDetails = constraintDetails.getJoinTableDetails();
				
				//if join table is present
				if(joinTableDetails != null)
				{
					//drop join table
					dataStore.dropTable(new DropTableQuery(joinTableDetails.toEntityDetails()));
				}
			}
		}
		
		//drop extended table if any
		if(entityDetails.getExtendedTableDetails() != null)
		{
			EntityDetails dummEntityDetails = new EntityDetails(entityDetails.getExtendedTableDetails().getTableName(), Object.class);
			dataStore.dropTable(new DropTableQuery(dummEntityDetails));
		}
		
		//drop main table
		dataStore.dropTable(new DropTableQuery(entityDetails));
		return null;
	}
	
	/**
	 * Gets actual repository type of this instance
	 * @param args
	 * @return
	 */
	private Object getRepositoryType(Object args[])
	{
		return repositoryType;
	}
	
	private Object setExecutionContext(Object args[])
	{
		queryExecutionContext.setRepositoryExecutionContext(args[0]);
		return null;
	}
}
