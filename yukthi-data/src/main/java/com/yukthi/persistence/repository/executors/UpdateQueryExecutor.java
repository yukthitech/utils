package com.yukthi.persistence.repository.executors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.FieldDetails;
import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.IDataStore;
import com.yukthi.persistence.ITransaction;
import com.yukthi.persistence.conversion.ConversionService;
import com.yukthi.persistence.listeners.EntityEventType;
import com.yukthi.persistence.query.ColumnParam;
import com.yukthi.persistence.query.QueryCondition;
import com.yukthi.persistence.query.UpdateColumnParam;
import com.yukthi.persistence.query.UpdateQuery;
import com.yukthi.persistence.repository.InvalidRepositoryException;
import com.yukthi.persistence.repository.annotations.Field;
import com.yukthi.persistence.repository.annotations.JoinOperator;
import com.yukthi.persistence.repository.annotations.Operator;
import com.yukthi.persistence.repository.annotations.UpdateFunction;
import com.yukthi.persistence.repository.annotations.UpdateOperator;

@QueryExecutorPattern(prefixes = {"update"}, annotatedWith = UpdateFunction.class)
public class UpdateQueryExecutor extends AbstractPersistQueryExecutor
{
	private static Logger logger = LogManager.getLogger(UpdateQueryExecutor.class);

	private Class<?> returnType;
	private ReentrantLock queryLock = new ReentrantLock();
	private boolean entityUpdate = false;
	private ConditionQueryBuilder conditionQueryBuilder;
	private String methodDesc;
	
	private UpdateQuery updateQuery;
	
	public UpdateQueryExecutor(Class<?> repositoryType, Method method, EntityDetails entityDetails)
	{
		super.entityDetails = entityDetails;
		super.repositoryType = repositoryType;
		
		conditionQueryBuilder = new ConditionQueryBuilder(entityDetails);
		methodDesc = String.format("update method '%s' of repository - '%s'", method.getName(), repositoryType.getName());
		
		
		Class<?> paramTypes[] = method.getParameterTypes();

		if(paramTypes == null || paramTypes.length == 0)
		{
			throw new InvalidRepositoryException("Zero parameter update method '" + method.getName() + "' in repository: " + repositoryType.getName());
		}
		
		boolean isCoreInterface = ICrudRepository.class.equals(method.getDeclaringClass());
		
		if( ( paramTypes.length == 1 && entityDetails.getEntityType().equals(paramTypes[0]) ) || isCoreInterface)
		{
			entityUpdate = true;
		}
		else
		{
			updateQuery = new UpdateQuery(entityDetails);
			
			if(!super.fetchConditonsByAnnotations(method, false, conditionQueryBuilder, methodDesc, false))
			{
				throw new InvalidRepositoryException("For non-entity update method '" + method.getName() + "' no conditions are specified, in repository: " + repositoryType.getName());
			}
			
			super.fetchMethodLevelConditions(method, conditionQueryBuilder, methodDesc);
			
			if(!fetchColumnsByAnnotations(method))
			{
				throw new InvalidRepositoryException("For non-entity update method '" + method.getName() + "' no columns are specified, in repository: " + repositoryType.getName());
			}
		}
		
		returnType = method.getReturnType();
		
		if(!boolean.class.equals(returnType) && !void.class.equals(returnType) && !int.class.equals(returnType))
		{
			throw new InvalidRepositoryException("Update method '" + method.getName() + "' found with non-boolean, non-void and non-int return type in repository: " + repositoryType.getName());
		}
	}
	
	private boolean fetchColumnsByAnnotations(Method method)
	{
		logger.trace("Started method: fetchColumnsByAnnotations");
		
		Class<?> paramTypes[] = method.getParameterTypes();
		Annotation paramAnnotations[][] = method.getParameterAnnotations();
		
		if(paramAnnotations == null)
		{
			return false;
		}

		Field field = null;
		boolean found = false;
		FieldDetails fieldDetails = null;
		
		//fetch conditions for each argument
		for(int i = 0; i < paramTypes.length; i++)
		{
			field = getAnnotation(paramAnnotations[i], Field.class);
			
			if(field == null)
			{
				continue;
			}
			
			fieldDetails = this.entityDetails.getFieldDetailsByField(field.value());
			
			if(fieldDetails == null)
			{
				throw new InvalidRepositoryException("@Field with invalid name '" + field.value() + "' is specified for update method '" 
						+ method.getName() + "' of repository: " + repositoryType.getName());
			}
			
			//if version field is specified explicitly for update
			if(fieldDetails.isVersionField())
			{
				throw new InvalidRepositoryException("Version field '{}' is configured for explicit update in repository method {}.{}", 
						field.value(), repositoryType.getName(), method.getName());
			}
			
			updateQuery.addColumn(new UpdateColumnParam(fieldDetails.getColumn(), null, i, field.updateOp()));
			found = true;
		}
		
		//add implicit version update instructions
		if(entityDetails.hasVersionField())
		{
			updateQuery.addColumn(new UpdateColumnParam(entityDetails.getVersionField().getColumn(), null, 1, UpdateOperator.ADD));
		}

		return found;
	}
	
	private Object updateFullEntity(IDataStore dataStore, ConversionService conversionService, Object entity)
	{
		logger.trace("Started method: updateFullEntity");
		
		if(entity == null)
		{
			throw new NullPointerException("Entity can not be null");
		}
		
		if(dataStore.isExplicitUniqueCheckRequired())
		{
			//check if unique constraints are getting violated
			checkForUniqueConstraints(dataStore, conversionService, entity, true);//TODO: Read only fields should be skipped
		}
		
		if(dataStore.isExplicitForeignCheckRequired())
		{
			//check if all foreign parent keys are available
			checkForForeignConstraints(dataStore, conversionService, entity);
		}

		UpdateQuery query = new UpdateQuery(entityDetails);
		Object value = null;
		
		for(FieldDetails field: entityDetails.getFieldDetails())
		{
			if(field.isIdField())
			{
				continue;
			}
			
			//if version field, add instruction to increment it
			if(field.isVersionField())
			{
				query.addColumn(new UpdateColumnParam(field.getColumn(), 1, -1, UpdateOperator.ADD));
				continue;
			}
			
			value = field.getValue(entity);
			
			//if current field is relation field
			if(field.isRelationField())
			{
				//if current table does not own relation, ignore current field
				if(!field.isTableOwned())
				{
					//TODO: Take care of cases where join table is involved
					continue;
				}
				
				if(value != null)
				{
					//if current table owns the relation in same table, replace the entity valu with foreign entity id value
					value = field.getForeignConstraintDetails().getTargetEntityDetails().getIdField().getValue(value);
				}
			}
			
			value = conversionService.convertToDBType(value, field);
			
			query.addColumn(new UpdateColumnParam(field.getColumn(), value, -1, UpdateOperator.NONE));
		}
		
		query.addCondition(new QueryCondition(null, entityDetails.getIdField().getColumn(), Operator.EQ, entityDetails.getIdField().getValue(entity), JoinOperator.AND, false));

		//if version field is defined on the entity add it to the condition
		if(entityDetails.hasVersionField())
		{
			query.addCondition(new QueryCondition(null, entityDetails.getVersionField().getColumn(), Operator.EQ, entityDetails.getVersionField().getValue(entity), JoinOperator.AND, false));
		}
		
		super.notifyEntityEvent(null, entity, EntityEventType.PRE_UPDATE);
		
		int res = dataStore.update(query, entityDetails);
		
		if(res > 0)
		{
			super.notifyEntityEvent(null, entity, EntityEventType.POST_UPDATE);
		}
		
		if(boolean.class.equals(returnType))
		{
			return (res > 0);
		}
		
		return (int.class.equals(returnType)) ? res : null;
		
	}

	
	@Override
	public Object execute(IDataStore dataStore, ConversionService conversionService, Object... params)
	{
		logger.trace("Started method: execute");
		
		if(entityUpdate)
		{
			return updateFullEntity(dataStore, conversionService, params[0]);
		}
		
		queryLock.lock();
		
		try
		{
			Object value = null;
			
			updateQuery.clearConditions();
			conditionQueryBuilder.loadConditionalQuery(updateQuery, params);
			
			//TODO: When unique fields are getting updated, make sure unique constraints are not violated
				//during unique field update might be we have to mandate id is provided as condition
			
			FieldDetails field = null;
			
			for(ColumnParam column: updateQuery.getColumns())
			{
				field = entityDetails.getFieldDetailsByColumn(column.getName());
				value = params[column.getIndex()];
				
				//if current field is relation field
				if(field.isRelationField())
				{
					//if current table does not own relation, ignore current field
					if(!field.isTableOwned())
					{
						//TODO: Take care of cases where join table is involved
						continue;
					}
					
					if(value != null)
					{
						//if current table owns the relation in same table, replace the entity value with foreign entity id value
						value = field.getForeignConstraintDetails().getTargetEntityDetails().getIdField().getValue(value);
					}
				}

				value = conversionService.convertToDBType(value, field);

				column.setValue(value);
			}
			
			try(ITransaction transaction = dataStore.getTransactionManager().newOrExistingTransaction())
			{
				int res = dataStore.update(updateQuery, entityDetails);

				transaction.commit();
				
				if(int.class.equals(returnType))
				{
					return res;
				}
				
				return (boolean.class.equals(returnType)) ? (res > 0) : null;
			}catch(Exception ex)
			{
				//rethrow the catched exception
				if(ex instanceof RuntimeException)
				{
					throw (RuntimeException)ex;
				}
				
				throw new IllegalStateException(ex);
			}
		}finally
		{
			queryLock.unlock();
		}
		
	}
}
