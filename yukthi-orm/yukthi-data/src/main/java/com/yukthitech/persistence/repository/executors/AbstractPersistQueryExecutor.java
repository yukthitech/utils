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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.ForeignConstraintDetails;
import com.yukthitech.persistence.ForeignConstraintViolationException;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.UniqueConstraintDetails;
import com.yukthitech.persistence.UniqueConstraintViolationException;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.query.AggregateQuery;
import com.yukthitech.persistence.query.QueryCondition;
import com.yukthitech.persistence.repository.annotations.AggregateFunctionType;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.utils.CommonUtils;

public abstract class AbstractPersistQueryExecutor extends QueryExecutor
{
	private static Logger logger = LogManager.getLogger(AbstractPersistQueryExecutor.class);
	
	private String formatMessage(String messageTemplate, Map<String, Object> context)
	{
		if(messageTemplate == null || messageTemplate.trim().length() == 0)
		{
			return null;
		}
		
		return CommonUtils.replaceExpressions(context, messageTemplate, null);
	}
	
	protected void checkForUniqueConstraints(IDataStore dataStore, ConversionService conversionService, Object entity, boolean excludeId)
	{
		logger.trace("Started method: checkForUniqueConstraints");
		
		AggregateQuery existenceQuery = new AggregateQuery(entityDetails, AggregateFunctionType.COUNT, entityDetails.getIdField().getDbColumnName());
		FieldDetails fieldDetails = null;
		Object value = null;
		String message = null;
		Map<String, Object> fieldValues = new HashMap<>();
		
		//validate unique constraint violation is not happening
		for(UniqueConstraintDetails uniqueConstraint: entityDetails.getUniqueConstraints())
		{
			if(!uniqueConstraint.isValidate())
			{
				continue;
			}
			
			existenceQuery.clearConditions();
			fieldValues.clear();
			
			for(String field: uniqueConstraint.getFields())
			{
				fieldDetails = entityDetails.getFieldDetailsByField(field);
				
				value = fieldDetails.getValue(entity);
				value = conversionService.convertToDBType(value, fieldDetails);
				
				existenceQuery.addCondition(new QueryCondition(null, fieldDetails.getDbColumnName(), Operator.EQ, value, JoinOperator.AND, false));
				fieldValues.put(field, value);
			}
			
			if(excludeId)
			{
				existenceQuery.addCondition(new QueryCondition(null, entityDetails.getIdField().getDbColumnName(), Operator.NE, entityDetails.getIdField().getValue(entity), JoinOperator.AND, false));
			}
			
			if(dataStore.fetchAggregateValue(existenceQuery, entityDetails) > 0)
			{
				message = formatMessage(uniqueConstraint.getMessage(), fieldValues);
				message = (message != null) ? message : "Unique constraint violated: " + uniqueConstraint.getName();
				
				throw new UniqueConstraintViolationException(entityDetails.getEntityType(), uniqueConstraint.getFields().toArray(new String[0]), 
						uniqueConstraint.getName(), message);
			}
		}
	}
	
	protected void checkForForeignConstraints(IDataStore dataStore, ConversionService conversionService, Object entity)
	{
		//if explicit foreign key validation is not required
		if(!dataStore.isExplicitForeignCheckRequired())
		{
			return;
		}
		
		logger.trace("Started method: checkForForeignConstraints");
		
		AggregateQuery existenceQuery = null;
		Object value = null;
		String message = null;
		EntityDetails foreignEntityDetails = null;
		
		FieldDetails ownerFieldDetails = null;
		
		//validate foreign constraint violation is not happening
		for(ForeignConstraintDetails foreignConstraint: entityDetails.getForeignConstraints())
		{
			//if current entity does not own this relation
			if(foreignConstraint.isMappedRelation())
			{
				continue;
			}
			
			//create existence query that needs to be executed against parent table
			existenceQuery = new AggregateQuery(foreignConstraint.getTargetEntityDetails(), AggregateFunctionType.COUNT, 
					foreignConstraint.getTargetEntityDetails().getIdField().getDbColumnName());

			foreignEntityDetails = foreignConstraint.getTargetEntityDetails();
			ownerFieldDetails = foreignEntityDetails.getFieldDetailsByField(foreignConstraint.getOwnerField().getName());

			value = ownerFieldDetails.getValue(entity);
			value = conversionService.convertToDBType(value, ownerFieldDetails);

			//if no value is defined for relationship
			if(value == null)
			{
				continue;
			}
			
			existenceQuery.addCondition(new QueryCondition(null, foreignEntityDetails.getIdField().getDbColumnName(), Operator.EQ, value, JoinOperator.AND, false));
			
			if(dataStore.fetchAggregateValue(existenceQuery, foreignEntityDetails) <= 0)
			{
				message = "Foreign constraint violated: " + foreignConstraint.getConstraintName();
				
				logger.error(message);
				throw new ForeignConstraintViolationException(entityDetails.getEntityType(), foreignConstraint.getConstraintName(), message);
			}
		}
	}
}
