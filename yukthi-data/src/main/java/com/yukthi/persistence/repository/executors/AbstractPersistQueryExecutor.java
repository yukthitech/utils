package com.yukthi.persistence.repository.executors;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.ccg.util.StringUtil;
import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.FieldDetails;
import com.yukthi.persistence.ForeignConstraintDetails;
import com.yukthi.persistence.ForeignConstraintViolationException;
import com.yukthi.persistence.IDataStore;
import com.yukthi.persistence.UniqueConstraintDetails;
import com.yukthi.persistence.UniqueConstraintViolationException;
import com.yukthi.persistence.conversion.ConversionService;
import com.yukthi.persistence.query.AggregateQuery;
import com.yukthi.persistence.query.QueryCondition;
import com.yukthi.persistence.repository.annotations.AggregateFunctionType;
import com.yukthi.persistence.repository.annotations.JoinOperator;
import com.yukthi.persistence.repository.annotations.Operator;

public abstract class AbstractPersistQueryExecutor extends QueryExecutor
{
	private static Logger logger = LogManager.getLogger(AbstractPersistQueryExecutor.class);
	
	private String formatMessage(String messageTemplate, Map<String, Object> context)
	{
		if(messageTemplate == null || messageTemplate.trim().length() == 0)
		{
			return null;
		}
		
		return StringUtil.getPatternString(messageTemplate, context);
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
