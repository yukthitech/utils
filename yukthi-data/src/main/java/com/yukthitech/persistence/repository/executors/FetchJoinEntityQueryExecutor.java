package com.yukthitech.persistence.repository.executors;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.ForeignConstraintDetails;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.JoinTableEntity;
import com.yukthitech.persistence.Record;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.query.FinderQuery;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.executors.builder.ConditionQueryBuilder;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Query executors to fetch child entities, whose relation is via join table.
 * @author akiran
 */
@QueryExecutorPattern(prefixes = {"find", "fetch"})
public class FetchJoinEntityQueryExecutor extends AbstractSearchQuery
{
	private static Logger logger = LogManager.getLogger(FetchJoinEntityQueryExecutor.class);
	
	private ReentrantLock queryLock = new ReentrantLock();
	
	/**
	 * @param entityDetails Parent entity details whose child entities has to be fetched
	 * @param fieldName field to fetch
	 */
	public FetchJoinEntityQueryExecutor(EntityDetails entityDetails, String fieldName)
	{
		FieldDetails fieldDetails = entityDetails.getFieldDetailsByField(fieldName);
		
		//if invalid field name is specified, or if the field is not multi-valued or join tabe based
		if(fieldDetails == null || !Collection.class.isAssignableFrom(fieldDetails.getField().getType()) || !fieldDetails.isTableJoined())
		{
			throw new InvalidArgumentException("Invalid field-name or non-joined field specified. [Field: '{}', Entity type: {}]", fieldName, entityDetails.getEntityType().getName());
		}
		
		methodDesc = String.format("join-entity fetched '%s.%s'", entityDetails.getEntityType().getName(), fieldName);

		ForeignConstraintDetails foreignConstraintDetails = fieldDetails.getForeignConstraintDetails();
		
		super.entityDetails = foreignConstraintDetails.getTargetEntityDetails();
		
		conditionQueryBuilder = new ConditionQueryBuilder(entityDetails);
		super.collectionReturnType = getCollectionType(fieldDetails.getField().getType(), methodDesc);
		super.returnType = super.entityDetails.getEntityType();

		//set all entity fields as result fields
		fetchEntityResultFields();
		
		//add join table condition
		EntityDetails joinTableDetails = foreignConstraintDetails.getJoinTableDetails().toEntityDetails();
		
		conditionQueryBuilder.addJoinTableCondition(0, null, JoinOperator.AND, methodDesc, joinTableDetails, 
				joinTableDetails.getFieldDetailsByField(JoinTableEntity.FIELD_JOIN_COLUMN), 
				joinTableDetails.getFieldDetailsByField(JoinTableEntity.FIELD_INV_JOIN_COLUMN));
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
			ConditionQueryBuilder conditionQueryBuilder = this.conditionQueryBuilder;
			
			FinderQuery finderQuery = new FinderQuery(entityDetails);

			//set the result fields, conditions and tables details on finder query
			conditionQueryBuilder.loadConditionalQuery(context, finderQuery, params);
			
			//add order-by fields
			conditionQueryBuilder.loadOrderByFields(finderQuery);
			
			//execute the query and fetch records
			List<Record> records = dataStore.executeFinder(finderQuery, entityDetails, null);
			
			//if no results found
			if(records == null || records.isEmpty())
			{
				try
				{
					return collectionReturnType.newInstance();
				}catch(Exception ex)
				{
					throw new IllegalStateException("An error occurred while creating return collection: " + collectionReturnType.getName(), ex);
				}
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
