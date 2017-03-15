package com.yukthi.persistence.repository.executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import com.yukthi.persistence.repository.executors.builder.ConditionQueryBuilder;

/**
 * This is similar to finder query executor but used fetch sub fields. 
 * @author akiran
 */
public class IntermediateQueryExecutor extends AbstractSearchQuery
{
	private static Logger logger = LogManager.getLogger(IntermediateQueryExecutor.class);
	
	private ReentrantLock queryLock = new ReentrantLock();
	
	private String mappingFieldCode;
	
	private String resultFieldCode;
	
	public IntermediateQueryExecutor(Class<?> repositoryType, EntityDetails entityDetails, String representation)
	{
		super.repositoryType = repositoryType;
		super.entityDetails = entityDetails;

		conditionQueryBuilder = new ConditionQueryBuilder(entityDetails);
		methodDesc = String.format("intermediate-finder method '%s' of repository - '%s'", representation, repositoryType.getName());
	}
	
	public void setMappingField(String field, Class<?> type)
	{
		this.mappingFieldCode = conditionQueryBuilder.addResultField("$MAPPING_FIELD", type, type, field, methodDesc);
	}
	
	public void setResultField(String field, Class<?> type)
	{
		this.resultFieldCode = conditionQueryBuilder.addResultField("$RESULT_FIELD", type, type, field, methodDesc);
	}
	
	void setReturnType(Class<?> returnType)
	{
		super.returnType = returnType;
	}
	
	void setReturnCollectionType(Class<?> returnCollectionType)
	{
		super.collectionReturnType = returnCollectionType;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.persistence.repository.executors.QueryExecutor#execute(com.yukthi.persistence.repository.executors.QueryExecutionContext, com.yukthi.persistence.IDataStore, com.yukthi.persistence.conversion.ConversionService, java.lang.Object[])
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
			conditionQueryBuilder.loadConditionalQuery(context.getRepositoryExecutionContext(), finderQuery, params);
			
			//add order-by fields
			conditionQueryBuilder.loadOrderByFields(finderQuery);
			
			//execute the query and fetch records
			List<Record> records = dataStore.executeFinder(finderQuery, entityDetails, null);
			
			//if no results found
			if(records == null || records.isEmpty())
			{
				//if primitive return type is expected simply return default value
				if(collectionReturnType == null)
				{
					return returnType.isPrimitive() ? CCGUtility.getDefaultPrimitiveValue(returnType) : null;
				}

				try
				{
					return collectionReturnType.newInstance();
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
