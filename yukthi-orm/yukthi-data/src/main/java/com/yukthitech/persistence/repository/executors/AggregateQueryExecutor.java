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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.query.AggregateQuery;
import com.yukthitech.persistence.repository.InvalidRepositoryException;
import com.yukthitech.persistence.repository.annotations.AggregateFunction;
import com.yukthitech.persistence.repository.annotations.AggregateFunctionType;
import com.yukthitech.persistence.repository.executors.builder.ConditionQueryBuilder;
import com.yukthitech.utils.ConvertUtils;

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
	 * @see com.yukthitech.persistence.repository.executors.QueryExecutor#execute(com.yukthitech.persistence.repository.executors.QueryExecutionContext, com.yukthitech.persistence.IDataStore, com.yukthitech.persistence.conversion.ConversionService, java.lang.Object[])
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
			conditionQueryBuilder.loadConditionalQuery(context, query, params);
			
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
