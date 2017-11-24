package com.yukthitech.persistence.repository.executors;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FilterAction;
import com.yukthitech.persistence.IDataFilter;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.IFinderRecordProcessor;
import com.yukthitech.persistence.Record;
import com.yukthitech.persistence.RecordCountMistmatchException;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.query.FinderQuery;
import com.yukthitech.persistence.repository.InvalidRepositoryException;
import com.yukthitech.persistence.repository.executors.builder.ConditionQueryBuilder;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;


/**
 * Query executors for finder queries.
 * @author akiran
 */
@QueryExecutorPattern(prefixes = {"find", "fetch"})
public class FinderQueryExecutor extends AbstractSearchQuery
{
	private static Logger logger = LogManager.getLogger(FinderQueryExecutor.class);
	
	private ReentrantLock queryLock = new ReentrantLock();
	
	private int customFieldsIndex = -1;
	
	/**
	 * Parameter index at which data filter can be expected.
	 */
	private int dataFilterIndex = -1;
	
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
		
		if(collectionReturnType != null)
		{
			checkForDataFilter(method);
		}
	}
	
	/**
	 * Checks if data filter is specified in method arguments, if specified sets {@link #dataFilterIndex} with the parameter index
	 * of the data filter.
	 * @param method mehod whose paramters needs to be searched for filter
	 */
	private void checkForDataFilter(Method method)
	{
		Type paramTypes[] = method.getGenericParameterTypes();
		ParameterizedType parameterizedType = null;
		
		for(int i = 0; i < paramTypes.length; i++)
		{
			if(!(paramTypes[i] instanceof ParameterizedType))
			{
				continue;
			}
			
			parameterizedType = (ParameterizedType) paramTypes[i];
			
			if(! IDataFilter.class.isAssignableFrom((Class<?>)parameterizedType.getRawType()) )
			{
				continue;
			}
			
			if(!TypeUtils.isAssignable(parameterizedType.getActualTypeArguments()[0], returnType))
			{
				throw new InvalidRepositoryException("Data-filter argument type '{}' is not matching with finder return type '{}'. [Method: {}, Repository: {}]",
						parameterizedType.getActualTypeArguments()[0].toString(), returnType.getName(), method.getName(), repositoryType.getName());
			}
			
			this.dataFilterIndex = i;
			break;
		}
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
			
			//list that will maintain final result beans
			final ArrayList<Object> resLst = new ArrayList<>();
			
			//identify the data filter
			final IDataFilter<Object> dataFilter = (dataFilterIndex < 0) ? null : (IDataFilter<Object>) params[dataFilterIndex];
			final ConditionQueryBuilder finalConditionQueryBuilder = conditionQueryBuilder; 

			//execute the query and fetch records
			dataStore.executeFinder(finderQuery, entityDetails, new IFinderRecordProcessor()
			{
				@Override
				public Action process(long recordNo, Record record)
				{
					if(collectionReturnType == null && recordNo > 2)
					{
						return Action.STOP;
					}
					
					Object recordBean = null;
					
					try
					{
						recordBean = finalConditionQueryBuilder.parseResult(record, returnType, conversionService, persistenceExecutionContext);
					}catch(Exception ex)
					{
						throw new InvalidStateException("An error occurred while converting record object into result bean of type: ", returnType.getName(), ex);
					}
					
					if(dataFilter != null)
					{
						FilterAction filterAction = dataFilter.filter(recordBean);
						filterAction = (filterAction == null) ? FilterAction.ACCEPT : filterAction;
						
						if(filterAction.isDataAccepted())
						{
							resLst.add(recordBean);
						}
						
						return filterAction.isStopProcessing() ? Action.STOP : Action.IGNORE; 
					}
					
					resLst.add( recordBean );
					return Action.IGNORE;
				}
			});
			
			
			//if no results found
			if(resLst.isEmpty())
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
				if(resLst.size() > 1)
				{
					throw new RecordCountMistmatchException("Multiple records found when single record is expected.");
				}
				
				return resLst.get(0);
			}
	
			//if collection of objects are expected as result
			Collection<Object> lst = null;
			
			try
			{
				lst = (Collection)collectionReturnType.newInstance();
				lst.addAll(resLst);
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while creating return collection: " + collectionReturnType.getName(), ex);
			}
			
			return lst;
		}finally
		{
			queryLock.unlock();
		}
	}
	
	
}
