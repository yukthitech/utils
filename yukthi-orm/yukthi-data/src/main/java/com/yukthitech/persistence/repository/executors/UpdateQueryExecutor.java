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
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.ExtendedTableDetails;
import com.yukthitech.persistence.ExtendedTableEntity;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.ITransaction;
import com.yukthitech.persistence.InvalidMappingException;
import com.yukthitech.persistence.annotations.NotUpdateable;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.listeners.EntityEventType;
import com.yukthitech.persistence.query.ColumnParam;
import com.yukthitech.persistence.query.FinderQuery;
import com.yukthitech.persistence.query.QueryCondition;
import com.yukthitech.persistence.query.QueryResultField;
import com.yukthitech.persistence.query.UpdateColumnParam;
import com.yukthitech.persistence.query.UpdateQuery;
import com.yukthitech.persistence.repository.InvalidRepositoryException;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.persistence.repository.annotations.OrderByField;
import com.yukthitech.persistence.repository.annotations.OrderByType;
import com.yukthitech.persistence.repository.annotations.QueryBean;
import com.yukthitech.persistence.repository.annotations.RelationUpdateType;
import com.yukthitech.persistence.repository.annotations.UpdateFunction;
import com.yukthitech.persistence.repository.annotations.UpdateOperator;
import com.yukthitech.persistence.repository.executors.builder.ConditionQueryBuilder;
import com.yukthitech.persistence.utils.OrmUtils;
import com.yukthitech.utils.ReflectionUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

@QueryExecutorPattern(prefixes = {"update"}, annotatedWith = UpdateFunction.class)
public class UpdateQueryExecutor extends AbstractPersistQueryExecutor
{
	private static Logger logger = LogManager.getLogger(UpdateQueryExecutor.class);

	private static class RelationUpdateParam 
	{
		int paramIndex;
		RelationUpdateType updateType;
		FieldDetails fieldDetails;
		String fieldName;

		RelationUpdateParam(int paramIndex, RelationUpdateType updateType, FieldDetails fieldDetails, String fieldName) 
		{
			this.paramIndex = paramIndex;
			this.updateType = updateType;
			this.fieldDetails = fieldDetails;
			this.fieldName = fieldName;
		}
	}

	private Class<?> returnType;
	private ReentrantLock queryLock = new ReentrantLock();
	private boolean entityUpdate = false;
	private ConditionQueryBuilder conditionQueryBuilder;
	private String methodDesc;
	
	private UpdateQuery updateQuery;
	
	private List<QueryResultField> orderByFields = null;
	
	private List<RelationUpdateParam> relationUpdateParams = new ArrayList<>();
	
	private RelationUpdateHandler relationUpdateHandler = new RelationUpdateHandler();
	
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
		Class<?> firstParamType = TypeUtils.getRawType(method.getGenericParameterTypes()[0], repositoryType);
		
		if( ( paramTypes.length >= 1 && entityDetails.getEntityType().equals(firstParamType) ) || isCoreInterface)
		{
			entityUpdate = true;
			
			super.fetchConditonsByAnnotations(method, false, conditionQueryBuilder, methodDesc, false);
			super.fetchMethodLevelConditions(method, conditionQueryBuilder, methodDesc, false);
		}
		else
		{
			updateQuery = new UpdateQuery(entityDetails);
			
			if(!super.fetchConditonsByAnnotations(method, false, conditionQueryBuilder, methodDesc, false))
			{
				throw new InvalidRepositoryException("For non-entity update method '" + method.getName() + "' no conditions are specified, in repository: " + repositoryType.getName());
			}
			
			super.fetchMethodLevelConditions(method, conditionQueryBuilder, methodDesc, false);
			
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
		
		fetchOrderDetails(method);
	}
	
	/**
	 * Fetches order by fields for the specified method.
	 * @param method Method from which order by details needs to be fetched.
	 */
	private void fetchOrderDetails(Method method)
	{
		OrderBy orderBy = recursiveAnnotationFactory.findAnnotationRecursively(method, OrderBy.class);
		
		if(orderBy == null)
		{
			return;
		}
		
		if(orderBy.fields().length > 0)
		{
			for(OrderByField field : orderBy.fields())
			{
				if(!entityDetails.hasField(field.name()))
				{
					throw new InvalidMappingException("Invalid field '" + field.name() + "' specified in @OrderBy annotation of finder method - " + methodDesc);
				}
				
				addOrdderBy(field.name(), field.type());
			}
		}
		else
		{
			for(String field : orderBy.value())
			{
				if(!entityDetails.hasField(field))
				{
					throw new InvalidMappingException("Invalid field '" + field + "' specified in @OrderBy annotation of finder method - " + methodDesc);
				}

				addOrdderBy(field, OrderByType.ASC);
			}
		}
	}
	
	/**
	 * Adds specified field to order by list.
	 * @param field Field to add
	 * @param orderByType Ordering type
	 */
	private void addOrdderBy(String field, OrderByType orderByType)
	{
		if(this.orderByFields == null)
		{
			this.orderByFields = new ArrayList<>();
		}

		FieldDetails fieldDetails = entityDetails.getFieldDetailsByField(field);
		this.orderByFields.add(new QueryResultField(null, fieldDetails.getDbColumnName(), null, orderByType));
	}
	
	private boolean fetchColumnsFromBean(Method method, int paramIndex, Class<?> paramType)
	{
		AtomicBoolean found = new AtomicBoolean(false);
		
		OrmUtils.processFields(paramType, beanField -> 
		{
			Field field = null;
			FieldDetails fieldDetails = null;
			
			field = beanField.getAnnotation(Field.class);
			
			if(field == null)
			{
				return;
			}
			
			// ignore not updateable fields.
			if(beanField.getAnnotation(NotUpdateable.class) != null)
			{
				return;
			}
			
			String fieldName = field.value();
			fieldName = StringUtils.isBlank(fieldName) ? beanField.getName() : fieldName;
			
			fieldDetails = this.entityDetails.getFieldDetailsByField(fieldName);
			
			if(fieldDetails == null)
			{
				throw new InvalidRepositoryException("On query bean field {}.{} @Field has invalid field-name '{}' is specified [Repository update method: {}.{}(), Parameter Index: {}, Param Field: {}]",
						paramType.getName(), beanField.getName(),
						fieldName, repositoryType.getName(), method.getName(), paramIndex, beanField.getName());
			}
			
			//if version field is specified explicitly for update
			if(fieldDetails.isVersionField())
			{
				throw new InvalidRepositoryException("Version field {}.{} (entity field: {}) is configured for explicit update [Repository update method: {}.{}(), Parameter Index: {}, Param Field: {}]", 
						paramType.getName(), beanField.getName(),
						fieldName, repositoryType.getName(), method.getName(), paramIndex, beanField.getName());
			}
			
			if(fieldDetails.isMultiValuedRelation() && field.relationUpdate() == RelationUpdateType.NONE)
			{
				throw new InvalidRepositoryException("Mulit-valued relation-field {}.{} (entity field: {}) is not configured with relationUpdate operator in @Field annotation [Repository update method: {}.{}(), Parameter Index: {}, Param Field: {}]", 
						paramType.getName(), beanField.getName(),
						fieldName, repositoryType.getName(), method.getName(), paramIndex, beanField.getName());
			}
			
			// Collect relation update info if specified for bean fields
			if(field.relationUpdate() != RelationUpdateType.NONE) 
			{
				addRelationParam(method, field, fieldDetails, paramIndex, beanField.getGenericType(), beanField.getName());
				found.set(true);
				return;
			}

			updateQuery.addColumn(new UpdateColumnParam(fieldDetails.getDbColumnName(), null, paramIndex, beanField.getName(), field.updateOp()));
			found.set(true);
		});
		
		return found.get();
	}


	private boolean fetchColumnsByAnnotations(Method method)
	{
		logger.trace("Started method: fetchColumnsByAnnotations");
		
		Parameter paramters[] = method.getParameters();
		
		Field field = null;
		QueryBean queryBean = null;
		boolean found = false;
		FieldDetails fieldDetails = null;
		
		//fetch conditions for each argument
		for(int i = 0; i < paramters.length; i++)
		{
			field = paramters[i].getAnnotation(Field.class);
			queryBean = paramters[i].getAnnotation(QueryBean.class);
			
			if(queryBean != null)
			{
				if(fetchColumnsFromBean(method, i, paramters[i].getType()))
				{
					found = true;
				}
				
				continue;
			}
			
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
			
			if(fieldDetails.isMultiValuedRelation() && field.relationUpdate() == RelationUpdateType.NONE)
			{
				throw new InvalidRepositoryException("Mulit-valued relation-field '{}' is not configured with relationUpdate operator in @Field annotation [Repository update method: {}.{}(), Parameter Index: {}]", 
						field.value(), repositoryType.getName(), method.getName(), i);
			}

			// Collect relation update info if specified
			if(field.relationUpdate() != RelationUpdateType.NONE) 
			{
				addRelationParam(method, field, fieldDetails, i, paramters[i].getParameterizedType(), null);
				found = true;
				continue;
			}
			
			updateQuery.addColumn(new UpdateColumnParam(fieldDetails.getDbColumnName(), null, i, field.updateOp()));
			found = true;
		}
		
		//add implicit version update instructions
		if(entityDetails.hasVersionField())
		{
			updateQuery.addColumn(new UpdateColumnParam(entityDetails.getVersionField().getDbColumnName(), 1, -1, UpdateOperator.ADD));
		}

		return found;
	}

	private void addRelationParam(Method method, Field field, FieldDetails fieldDetails, int paramIdx, Type paramType, String fieldName)
	{
		if(!fieldDetails.isRelationField()) 
		{
			throw new InvalidRepositoryException("@Field with relationUpdate specified must represent a relation field. Field: '{}', Method: {}.{}()", 
					field.value(), repositoryType.getName(), method.getName());
		}

		if(!Collection.class.isAssignableFrom(TypeUtils.getRawType(paramType, null)))
		{
			throw new InvalidRepositoryException("@Field with relationUpdate specified must represent a collection. Field: '{}', Method: {}.{}()", 
					field.value(), repositoryType.getName(), method.getName());
		}

		ParameterizedType parameterizedType = (ParameterizedType) paramType;
		Class<?> collectionType = (Class<?>) parameterizedType.getActualTypeArguments()[0];

		if(!fieldDetails.getForeignConstraintDetails().getTargetEntityDetails().getEntityType().isAssignableFrom(collectionType))
		{
			throw new InvalidRepositoryException("@Field with relationUpdate specified must represent a collection of same entities. Field: '{}', Method: {}.{}()", 
					field.value(), repositoryType.getName(), method.getName());
		}
		
		relationUpdateParams.add(new RelationUpdateParam(paramIdx, field.relationUpdate(), fieldDetails, fieldName));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateExtensionFields(IDataStore dataStore, ConversionService conversionService, Object entity)
	{
		ExtendedTableDetails extendedTableDetails = entityDetails.getExtendedTableDetails();
		
		if(extendedTableDetails == null)
		{
			return;
		}
		
		java.lang.reflect.Field extDataHolder = extendedTableDetails.getEntityField();
		
		Map<String, Object> extendedValues = null;
		
		try
		{
			extDataHolder.setAccessible(true);
			extendedValues = (Map)extDataHolder.get(entity);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching extended values", ex);
		}
		
		if(extendedValues == null || extendedValues.isEmpty())
		{
			return;
		}
		
		Object id = entityDetails.getIdField().getValue(entity);
		
		UpdateQuery updateQuery = new UpdateQuery(extendedTableDetails.toEntityDetails(entityDetails));
		
		for(String field : extendedValues.keySet())
		{
			updateQuery.addColumn(new UpdateColumnParam(field.toLowerCase(), extendedValues.get(field), -1, UpdateOperator.NONE));
		}
		
		updateQuery.addCondition(new QueryCondition(null, ExtendedTableEntity.COLUMN_ENTITY_ID, Operator.EQ, id, null, false));
		
		if( dataStore.update(updateQuery, extendedTableDetails.toEntityDetails(entityDetails)) <= 0)
		{
			throw new InvalidStateException("An error occurred while updating extension fields");
		}
	}
	
	private Object updateFullEntity(QueryExecutionContext context, IDataStore dataStore, ConversionService conversionService, Object... params)
	{
		logger.trace("Started method: updateFullEntity");
		
		Object entity = params[0];
		
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
				query.addColumn(new UpdateColumnParam(field.getDbColumnName(), 1, -1, UpdateOperator.ADD));
				continue;
			}
			
			//if field is not updateable skip the field
			if(!field.isUpdateable())
			{
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
			
			query.addColumn(new UpdateColumnParam(field.getDbColumnName(), value, -1, UpdateOperator.NONE));
		}
		
		query.addCondition(new QueryCondition(null, entityDetails.getIdField().getDbColumnName(), Operator.EQ, entityDetails.getIdField().getValue(entity), JoinOperator.AND, false));

		//if version field is defined on the entity add it to the condition
		if(entityDetails.hasVersionField())
		{
			query.addCondition(new QueryCondition(null, entityDetails.getVersionField().getDbColumnName(), Operator.EQ, entityDetails.getVersionField().getValue(entity), JoinOperator.AND, false));
		}
		
		conditionQueryBuilder.loadConditionalQuery(context, query, params);
		
		try(ITransaction transaction = dataStore.getTransactionManager().newOrExistingTransaction())
		{
			super.notifyEntityEvent(null, entity, EntityEventType.PRE_UPDATE);
			
			int res = dataStore.update(query, entityDetails);
			
			updateExtensionFields(dataStore, conversionService, entity);
			
			if(res > 0)
			{
				super.notifyEntityEvent(null, entity, EntityEventType.POST_UPDATE);
			}
			
			transaction.commit();

			if(boolean.class.equals(returnType))
			{
				return (res > 0);
			}
			
			return (int.class.equals(returnType)) ? res : null;
		}catch(Exception ex)
		{
			//rethrow the catched exception
			if(ex instanceof RuntimeException)
			{
				throw (RuntimeException)ex;
			}
			
			throw new IllegalStateException(ex);
		}
	}
	
	private Object computeParamValue(Object value, String fieldName, FieldDetails field, ConversionService conversionService)
	{
		if(StringUtils.isNotBlank(fieldName))
		{
			try
			{
				value = ReflectionUtils.getFieldValue(value, fieldName);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while fetching param-query-bean field name: {}", 
						fieldName, ex);
			}
		}
		
		//if current field is relation field
		if(field.isRelationField() && field.isTableOwned() && value != null && !(value instanceof Long))
		{
			//if current table owns the relation in same table, replace the entity value with foreign entity id value
			value = field.getForeignConstraintDetails().getTargetEntityDetails().getIdField().getValue(value);
		}

		value = conversionService.convertToDBType(value, field);
		return value;
	}

	
	/* (non-Javadoc)
	 * @see com.yukthitech.persistence.repository.executors.QueryExecutor#execute(com.yukthitech.persistence.repository.executors.QueryExecutionContext, com.yukthitech.persistence.IDataStore, com.yukthitech.persistence.conversion.ConversionService, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(QueryExecutionContext context, IDataStore dataStore, ConversionService conversionService, Object... params)
	{
		logger.trace("Started method: execute");
		
		if(entityUpdate)
		{
			return updateFullEntity(context, dataStore, conversionService, params);
		}
		
		queryLock.lock();
		
		try
		{
			Object value = null;
			
			updateQuery.clearConditions();
			conditionQueryBuilder.loadConditionalQuery(context, updateQuery, params);
			
			//add order-by fields
			if(orderByFields != null)
			{
				for(QueryResultField orderField : this.orderByFields)
				{
					updateQuery.addOrderByField(orderField);
				}
			}

			//TODO: When unique fields are getting updated, make sure unique constraints are not violated
				//during unique field update might be we have to mandate id is provided as condition
			
			//TODO: Extension field update using annotations
			FieldDetails field = null;
			
			for(ColumnParam column: updateQuery.getColumns())
			{
				field = entityDetails.getFieldDetailsByColumn(column.getName());
				
				//index would be less than internal fields like version
				if(column.getIndex() < 0)
				{
					continue; 
				}
				
				value = params[column.getIndex()];
				value = computeParamValue(value, column.getFieldName(), field, conversionService);
				column.setValue(value);
			}
			
			try(ITransaction transaction = dataStore.getTransactionManager().newOrExistingTransaction())
			{
				int res = 0;
				
				if(updateQuery.getColumns().size() > 0)
				{
					res = dataStore.update(updateQuery, entityDetails);
				}

				// Integrate relation update handler after main update
				if(!relationUpdateParams.isEmpty())
				{
					// Create a FinderQuery from the UpdateQuery's conditions
					FinderQuery finderQuery = new FinderQuery(entityDetails);
					finderQuery.setDefaultTableCode(updateQuery.getDefaultTableCode());
					
					finderQuery.addResultField(new QueryResultField(finderQuery.getDefaultTableCode(), entityDetails.getIdField().getDbColumnName(), "id"));

					for(var cond : updateQuery.getConditions())
					{
						finderQuery.addCondition(cond);
					}

					for(RelationUpdateParam relParam : relationUpdateParams)
					{
						Object relValue = params[relParam.paramIndex];
						relValue = computeParamValue(relValue, relParam.fieldName, relParam.fieldDetails, conversionService);

						res += relationUpdateHandler.handleRelationUpdate(context.getRepositoryFactory(),
								finderQuery, entityDetails, relParam.fieldDetails, relParam.updateType, (Collection<Object>) relValue);
					}
				}

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
