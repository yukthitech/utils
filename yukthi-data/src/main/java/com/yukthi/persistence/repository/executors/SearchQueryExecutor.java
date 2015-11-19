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
import com.yukthi.persistence.IFinderRecordProcessor;
import com.yukthi.persistence.Record;
import com.yukthi.persistence.RecordCountMistmatchException;
import com.yukthi.persistence.conversion.ConversionService;
import com.yukthi.persistence.query.FinderQuery;
import com.yukthi.persistence.repository.InvalidRepositoryException;
import com.yukthi.persistence.repository.annotations.SearchFunction;
import com.yukthi.persistence.repository.search.SearchCondition;
import com.yukthi.persistence.repository.search.SearchQuery;

@QueryExecutorPattern(prefixes = {"search"}, annotatedWith = SearchFunction.class)
public class SearchQueryExecutor extends AbstractSearchQuery
{
	private static Logger logger = LogManager.getLogger(SearchQueryExecutor.class);
	
	private ReentrantLock queryLock = new ReentrantLock();
	
	public SearchQueryExecutor(Class<?> repositoryType, Method method, EntityDetails entityDetails)
	{
		super.repositoryType = repositoryType;
		super.entityDetails = entityDetails;

		conditionQueryBuilder = new ConditionQueryBuilder(entityDetails);
		methodDesc = String.format("search method '%s' of repository - '%s'", method.getName(), repositoryType.getName());

		Class<?> paramTypes[] = method.getParameterTypes();

		if(paramTypes.length != 1 && SearchQuery.class.equals(paramTypes[0]))
		{
			throw new InvalidRepositoryException("Invalid parameters specified for search method. Search method should have single parameter and it should of type - " + SearchQuery.class.getName());
		}
		
		fetchReturnDetails(method);
		super.fetchOrderDetails(method);
	}
	
	/**
	 * Adds search condition recursively to specified builder
	 * @param condition
	 * @param conditionQueryBuilder
	 * @param conditionParams
	 * @param groupHead
	 */
	private void addConditionsRecursively(SearchCondition condition, ConditionQueryBuilder conditionQueryBuilder, List<Object> conditionParams, ConditionQueryBuilder.Condition groupHead)
	{
		ConditionQueryBuilder.Condition builderCondition = null;
		builderCondition = conditionQueryBuilder.addCondition(groupHead, condition.getOperator(), conditionParams.size(), null, 
				condition.getField(), condition.getJoinOperator(), methodDesc, condition.isNullable());
		
		conditionParams.add(condition.getValue());
		
		//if no group conditions are present ignore
		if(condition.getGroupedConditions() == null)
		{
			return;
		}
		
		//add group conditions recursively
		for(SearchCondition grpCondition : condition.getGroupedConditions())
		{
			addConditionsRecursively(grpCondition, conditionQueryBuilder, conditionParams, builderCondition);
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
			SearchQuery searchQuery = (SearchQuery)params[0];
			
			//create a clone so that every time dynamic conditions can be added freshly
			ConditionQueryBuilder conditionQueryBuilder = this.conditionQueryBuilder.clone();
			
			//set the result fields, conditions and tables details on finder query
			List<Object> conditionParams = new ArrayList<>();

			//add conditions to query builder so that they will be validated
			for(SearchCondition condition : searchQuery.getConditions())
			{
				addConditionsRecursively(condition, conditionQueryBuilder, conditionParams, null);
			}
			
			logger.debug("Executing search query with params - {}", conditionParams);
			
			//add order-by fields
			if(searchQuery.getOrderByFields() != null)
			{
				conditionQueryBuilder.clearOrderByFields();
				
				for(String field : searchQuery.getOrderByFields())
				{
					conditionQueryBuilder.addOrderByField(field, methodDesc);
				}
			}

			//load condition values
			conditionQueryBuilder.loadConditionalQuery(finderQuery, conditionParams.toArray());
			conditionQueryBuilder.loadOrderByFields(finderQuery);
			
			IFinderRecordProcessor recordCountLimiter = null;

			//if results needs to be limited
			if(searchQuery.getResultsLimitCount() > 0)
			{
				finderQuery.setResultsLimitCount(searchQuery.getResultsLimitCount());
				
				//As some of the DB's like DB does not support limit, explicit processor 
				//	is added to stop records fetching after the limit
				long recordLimit = searchQuery.getResultsLimitCount();
				
				recordCountLimiter = new IFinderRecordProcessor()
				{
					@Override
					public boolean process(long recordNo, Record record)
					{
						return (recordNo <= recordLimit);
					}
				};
			}
			
			
			//execute the query and fetch records
			List<Record> records = dataStore.executeFinder(finderQuery, entityDetails, recordCountLimiter);
			
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
