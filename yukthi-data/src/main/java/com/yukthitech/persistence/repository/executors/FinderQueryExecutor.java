package com.yukthitech.persistence.repository.executors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.Record;
import com.yukthitech.persistence.RecordCountMistmatchException;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.query.FinderQuery;
import com.yukthitech.persistence.repository.executors.builder.ConditionQueryBuilder;
import com.yukthitech.utils.CommonUtils;

@QueryExecutorPattern(prefixes = {"find", "fetch"})
public class FinderQueryExecutor extends AbstractSearchQuery
{
	private static Logger logger = LogManager.getLogger(FinderQueryExecutor.class);
	
	private ReentrantLock queryLock = new ReentrantLock();
	
	private int customFieldsIndex = -1;
	
	public FinderQueryExecutor(Class<?> repositoryType, Method method, EntityDetails entityDetails)
	{
		super.repositoryType = repositoryType;
		super.entityDetails = entityDetails;

		conditionQueryBuilder = new ConditionQueryBuilder(entityDetails);
		methodDesc = String.format("finder method '%s' of repository - '%s'", method.getName(), repositoryType.getName());

		fetchReturnDetails(method);
		
		if( !fetchConditonsByAnnotations(method, true, conditionQueryBuilder, methodDesc, true) )
		{
			fetchConditionsByName(method, conditionQueryBuilder, methodDesc);
		}
		
		fetchMethodLevelConditions(method, conditionQueryBuilder, methodDesc, true);
		
		super.fetchOrderDetails(method);
		
		customFieldsIndex = super.getExtendedFieldParam(method);
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
			
			if(this.customFieldsIndex >= 0)
			{
				Collection<String> customFieldNames = (Collection<String>) params[customFieldsIndex];
				
				if(customFieldNames != null && !customFieldNames.isEmpty())
				{
					conditionQueryBuilder = this.conditionQueryBuilder.clone();
					String prefix = entityDetails.getExtendedTableDetails().getEntityField().getName();
					
					for(String custFld : customFieldNames)
					{
						conditionQueryBuilder.addResultField("@" + custFld, String.class, String.class, prefix + "." + custFld, super.methodDesc);
					}
				}
			}
			
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
					return returnType.isPrimitive() ? CommonUtils.getDefaultValue(returnType) : null;
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
