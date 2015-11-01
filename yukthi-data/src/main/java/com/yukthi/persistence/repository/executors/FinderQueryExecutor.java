package com.yukthi.persistence.repository.executors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

@QueryExecutorPattern(prefixes = {"find", "fetch"})
public class FinderQueryExecutor extends AbstractSearchQuery
{
	private static Logger logger = LogManager.getLogger(FinderQueryExecutor.class);
	
	private ReentrantLock queryLock = new ReentrantLock();
	
	public FinderQueryExecutor(Class<?> repositoryType, Method method, EntityDetails entityDetails)
	{
		super.repositoryType = repositoryType;
		super.entityDetails = entityDetails;

		conditionQueryBuilder = new ConditionQueryBuilder(entityDetails);
		methodDesc = String.format("finder method '%s' of repository - '%s'", method.getName(), repositoryType.getName());

		/*
		Class<?> paramTypes[] = method.getParameterTypes();

		if(paramTypes.length == 0)
		{
			throw new InvalidRepositoryException("No-parameter finder method '" + method.getName() + "' in repository: " + repositoryType.getName());
		}
		*/
		
		fetchReturnDetails(method);
		
		/*
		if(!fetchConditonsByAnnotations(method, true, conditionQueryBuilder, methodDesc, true) && 
				!fetchConditionsByName(method, conditionQueryBuilder, methodDesc))
		{
			throw new InvalidRepositoryException("Failed to determine parameter conditions for finder method '" 
							+ method.getName() + "' of repository - " + repositoryType.getName());
		}
		*/
		
		if( !fetchConditonsByAnnotations(method, true, conditionQueryBuilder, methodDesc, true) )
		{
			fetchConditionsByName(method, conditionQueryBuilder, methodDesc);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Object execute(IDataStore dataStore, ConversionService conversionService, Object... params)
	{
		logger.trace("Started method: execute");
		
		queryLock.lock();
		
		try
		{
			FinderQuery finderQuery = new FinderQuery(entityDetails);

			//set the result fields, conditions and tables details on finder query
			conditionQueryBuilder.loadConditionalQuery(finderQuery, params);
			
			//execute the query and fetch records
			List<Record> records = dataStore.executeFinder(finderQuery, entityDetails);
			
			//if no results found
			if(records == null || records.isEmpty())
			{
				//if primitive return type is expected simply return default value
				if(collectionReturnType == null)
				{
					return returnType.isPrimitive() ? CCGUtility.getDefaultPrimitiveValue(returnType) : null;
				}
				
				return Collections.emptyList();
			}

			//if single element is expected as result
			if(collectionReturnType == null)
			{
				if(records.size() > 1)
				{
					throw new RecordCountMistmatchException("Multiple records found when single record is expected.");
				}
				
				ArrayList<Object> resLst = new ArrayList<>();
				conditionQueryBuilder.parseResults(Arrays.asList(records.get(0)), (Class)returnType, resLst, conversionService, persistenceExecutionContext);
				return resLst.get(0);
			}
	
			//if collection of objects are expected as result
			Collection<Object> lst = null;
			
			try
			{
				lst = (Collection)collectionReturnType.newInstance();
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while creating return collection: " + collectionReturnType.getName(), ex);
			}
			
			//parse records into required types
			conditionQueryBuilder.parseResults(records, (Class)returnType, lst, conversionService, persistenceExecutionContext);
			
			return lst;
		}finally
		{
			queryLock.unlock();
		}
	}
	
	
}
