package com.yukthitech.persistence.repository.executors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.IFinderRecordProcessor;
import com.yukthitech.persistence.OrderByField;
import com.yukthitech.persistence.Record;
import com.yukthitech.persistence.RecordCountMistmatchException;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.query.AggregateQuery;
import com.yukthitech.persistence.query.FinderQuery;
import com.yukthitech.persistence.repository.InvalidRepositoryException;
import com.yukthitech.persistence.repository.annotations.AggregateFunctionType;
import com.yukthitech.persistence.repository.annotations.SearchFunction;
import com.yukthitech.persistence.repository.executors.builder.ConditionQueryBuilder;
import com.yukthitech.persistence.repository.search.IDynamicSearchResult;
import com.yukthitech.persistence.repository.search.SearchCondition;
import com.yukthitech.persistence.repository.search.SearchQuery;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;

@QueryExecutorPattern(prefixes = {"search"}, annotatedWith = SearchFunction.class)
public class SearchQueryExecutor extends AbstractSearchQuery
{
	private static Logger logger = LogManager.getLogger(SearchQueryExecutor.class);
	
	private ReentrantLock queryLock = new ReentrantLock();
	
	private Class<?> countReturnType;
	
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
		
		countReturnType = method.getReturnType();
		
		if(Long.class.equals(countReturnType) || long.class.equals(countReturnType) || Integer.class.equals(countReturnType) || int.class.equals(countReturnType))
		{
			//nothing do here for now
		}
		else
		{
			countReturnType = null;
			fetchReturnDetails(method);
		}
		
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
		boolean ignoreCase = ((condition.getValue() instanceof String) && condition.isIgnoreCase());
		
		Object conditionValue = condition.getValue();
		
		if(conditionValue instanceof SearchQuery)
		{
			SearchQuery searchQuery = (SearchQuery) conditionValue;
			
			ConditionQueryBuilder.InnerQuery innerQuery = conditionQueryBuilder.addSubsearchQuery(groupHead, condition.getField(), condition.getJoinOperator(), 
					methodDesc, searchQuery, super.persistenceExecutionContext.getRepositoryFactory());
			
			for(SearchCondition searchCondition : searchQuery.getConditions())
			{
				addConditionsRecursively(searchCondition, innerQuery.getSubqueryBuilder(), conditionParams, null);
			}
			
			return;
		}
		else
		{
			builderCondition = conditionQueryBuilder.addCondition(groupHead, condition.getOperator(), conditionParams.size(), null, 
					condition.getField(), condition.getJoinOperator(), methodDesc, condition.isNullable(), ignoreCase, null);
		}
		
		conditionParams.add(conditionValue);
		
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
	
	private Long findCount(QueryExecutionContext context, IDataStore dataStore, ConversionService conversionService, Object... params)
	{
		AggregateQuery countQuery = new AggregateQuery(entityDetails, AggregateFunctionType.COUNT, entityDetails.getIdField().getDbColumnName());
		SearchQuery searchQuery = (SearchQuery)params[0];
		
		//create a clone so that every time dynamic conditions can be added freshly
		ConditionQueryBuilder conditionQueryBuilder = this.conditionQueryBuilder.clone();
		
		List<Object> conditionParams = new ArrayList<>();

		//add conditions to query builder so that they will be validated
		for(SearchCondition condition : searchQuery.getConditions())
		{
			addConditionsRecursively(condition, conditionQueryBuilder, conditionParams, null);
		}
		
		//load condition values
		conditionQueryBuilder.loadConditionalQuery(context.getRepositoryExecutionContext(), countQuery, conditionParams.toArray());

		logger.debug("Executing search query with params - {}", conditionParams);

		return dataStore.fetchAggregateValue(countQuery, entityDetails).longValue();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.persistence.repository.executors.QueryExecutor#execute(com.yukthitech.persistence.repository.executors.QueryExecutionContext, com.yukthitech.persistence.IDataStore, com.yukthitech.persistence.conversion.ConversionService, java.lang.Object[])
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Object execute(QueryExecutionContext context, IDataStore dataStore, ConversionService conversionService, Object... params)
	{
		logger.trace("Started method: execute");
		
		queryLock.lock();
		
		try
		{
			if(countReturnType != null)
			{
				Long count = findCount(context, dataStore, conversionService, params);
				return ConvertUtils.convert(count, countReturnType);
			}

			final FinderQuery finderQuery = new FinderQuery(entityDetails);
			SearchQuery searchQuery = (SearchQuery)params[0];
			
			//create a clone so that every time dynamic conditions can be added freshly
			ConditionQueryBuilder conditionQueryBuilder = this.conditionQueryBuilder.clone();
			
			//remove result fields, which are excluded explicitly
			if(CollectionUtils.isNotEmpty(searchQuery.getExcludeFields()))
			{
				for(String excludedProperty : searchQuery.getExcludeFields())
				{
					conditionQueryBuilder.removeResultField(excludedProperty);
				}
			}
			
			//if return type dynamic field data
			if(IDynamicSearchResult.class.isAssignableFrom(returnType))
			{
				//if search query has additional field details to fetch
				if(CollectionUtils.isNotEmpty(searchQuery.getAdditionalEntityFields()))
				{
					//add dynamic fields to condition builder
					for(String additionalProp : searchQuery.getAdditionalEntityFields())
					{
						conditionQueryBuilder.addResultField("#" + additionalProp, Object.class, Object.class, additionalProp, "<Addidional Property> - " + additionalProp);
					}
				}
			}
			
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
				
				for(OrderByField field : searchQuery.getOrderByFields())
				{
					conditionQueryBuilder.addOrderByField(field.getName(), field.getOrderByType(), methodDesc);
				}
			}

			//load condition values
			conditionQueryBuilder.loadConditionalQuery(context.getRepositoryExecutionContext(), finderQuery, conditionParams.toArray());
			conditionQueryBuilder.loadOrderByFields(finderQuery);
			
			IFinderRecordProcessor recordCountLimiter = null;

			//if results needs to be limited
			if(searchQuery.getResultsLimit() > 0 || searchQuery.getResultsOffset() > 0)
			{
				int start = searchQuery.getResultsOffset();
				int countLimit = searchQuery.getResultsLimit();
				
				start = (start <= 0) ? 0 : start;
				countLimit = (countLimit <= 0) ? Integer.MAX_VALUE : countLimit;
				
				
				finderQuery.setResultsOffset(start);
				finderQuery.setResultsLimit(countLimit);
				
				//As some of the DB's like Derby does not support limit, explicit processor 
				//	is added to stop records fetching after the limit
				if(!dataStore.isPagingSupported())
				{
					recordCountLimiter = new IFinderRecordProcessor()
					{
						int recStart = finderQuery.getResultsOffset();
						int count = 0;
						int recLimit = finderQuery.getResultsLimit();
						
						@Override
						public Action process(long recordNo, Record record)
						{
							if(recordNo < recStart)
							{
								return Action.IGNORE;
							}
							
							count++;
							
							return (count <= recLimit) ? Action.PROCESS : Action.STOP;
						}
					};
				}
			}
			
			//execute the query and fetch records
			List<Record> records = dataStore.executeFinder(finderQuery, entityDetails, recordCountLimiter);
			
			//if no results found
			if(records == null || records.isEmpty())
			{
				//if primitive return type is expected simply return default value
				if(collectionReturnType == null)
				{
					return returnType.isPrimitive() ? CommonUtils.getDefaultValue(returnType) : null;
				}
				
				try
				{
					return (Collection)collectionReturnType.newInstance();
				}catch(Exception ex)
				{
					throw new IllegalStateException("An error occurred while creating return collection: " + collectionReturnType.getName(), ex);
				}
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
