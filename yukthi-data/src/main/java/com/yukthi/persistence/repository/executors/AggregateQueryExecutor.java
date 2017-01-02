package com.yukthi.persistence.repository.executors;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.FieldDetails;
import com.yukthi.persistence.IDataStore;
import com.yukthi.persistence.conversion.ConversionService;
import com.yukthi.persistence.query.AggregateQuery;
import com.yukthi.persistence.repository.InvalidRepositoryException;
import com.yukthi.persistence.repository.annotations.AggregateFunction;
import com.yukthi.persistence.repository.annotations.AggregateFunctionType;
import com.yukthi.persistence.repository.executors.builder.ConditionQueryBuilder;
import com.yukthi.utils.ConvertUtils;

/**
 * Executor for count queries. Count query methods can have following return types - Boolean, int or long
 * @author akiran
 */
@QueryExecutorPattern(annotatedWith = AggregateFunction.class)
public class AggregateQueryExecutor extends QueryExecutor
{
	private static Logger logger = LogManager.getLogger(AggregateQueryExecutor.class);
	
	/**
	 * Support return types by this query executor
	 */
	private static Set<Class<?>> SUPPORTED_RETURN_TYPES = new HashSet<>(Arrays.asList(
			Boolean.class, boolean.class,
			Long.class, long.class,
			Integer.class, int.class,
			Float.class, float.class,
			Double.class, double.class
	));
	
	private ReentrantLock queryLock = new ReentrantLock();
	private Class<?> returnType;
	private ConditionQueryBuilder conditionQueryBuilder;
	private String methodDesc;
	
	/**
	 * Function type to use.
	 */
	private AggregateFunctionType functionType;
	
	/**
	 * Column to use in aggregate function.
	 */
	private String column;
	
	public AggregateQueryExecutor(Class<?> repositoryType, Method method, EntityDetails entityDetails)
	{
		super.repositoryType = repositoryType;
		super.entityDetails = entityDetails;
		
		conditionQueryBuilder = new ConditionQueryBuilder(entityDetails);
		this.methodDesc = String.format("Aggregate query '%s' of entity type - '%s'", method.getName(), repositoryType.getName());
		
		//find conditions based on annotations
		if(!super.fetchConditonsByAnnotations(method, true, conditionQueryBuilder, methodDesc, true))
		{
			//if based on annotations not found, check based on function name
			super.fetchConditionsByName(method, conditionQueryBuilder, methodDesc);
		}
		
		super.fetchMethodLevelConditions(method, conditionQueryBuilder, methodDesc, true);
		
		this.returnType = method.getReturnType();
		
		if(!SUPPORTED_RETURN_TYPES.contains(returnType))
		{
			throw new InvalidRepositoryException("Invalid return type encountered for count method '" 
					+ method.getName() + "' of repository - " + repositoryType.getName() + " (expected return type - boolean, long or int)");
		}
		
		AggregateFunction aggregateFunction = method.getAnnotation(AggregateFunction.class);
		this.functionType = aggregateFunction.type();
		
		String field = aggregateFunction.field();
		
		if(field.trim().length() == 0)
		{
			this.column = entityDetails.getIdField().getDbColumnName();
		}
		else
		{
			FieldDetails fieldDetails = entityDetails.getFieldDetailsByField(field);
			
			if(fieldDetails == null)
			{
				throw new InvalidRepositoryException("Invalid field name '{}' specified in repository function - {}.{}()", field, repositoryType.getName(), method.getName());
			}
			
			this.column = fieldDetails.getDbColumnName();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.persistence.repository.executors.QueryExecutor#execute(com.yukthi.persistence.repository.executors.QueryExecutionContext, com.yukthi.persistence.IDataStore, com.yukthi.persistence.conversion.ConversionService, java.lang.Object[])
	 */
	@Override
	public Object execute(QueryExecutionContext context, IDataStore dataStore, ConversionService conversionService, Object... params)
	{
		logger.trace("Started method: execute");
		
		queryLock.lock();
		
		try
		{
			AggregateQuery query = new AggregateQuery(entityDetails, functionType, column);
			
			//set condition values on query
			conditionQueryBuilder.loadConditionalQuery(context.getRepositoryExecutionContext(), query, params);
			
			//execute the query and fetch result count
			Double aggrValue = dataStore.fetchAggregateValue(query, entityDetails);
			
			//if return type is boolean
			if(boolean.class.equals(this.returnType) || Boolean.class.equals(this.returnType))
			{
				return (aggrValue.longValue() > 0);
			}
			
			//convert the count to required return type
			return ConvertUtils.convert(aggrValue, returnType);
		}finally
		{
			queryLock.unlock();
		}
	}
	
	
}
