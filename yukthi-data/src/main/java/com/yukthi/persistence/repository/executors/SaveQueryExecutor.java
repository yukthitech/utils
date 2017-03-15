package com.yukthi.persistence.repository.executors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.GenerationType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.ExtendedTableDetails;
import com.yukthi.persistence.ExtendedTableEntity;
import com.yukthi.persistence.FieldDetails;
import com.yukthi.persistence.ForeignConstraintDetails;
import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.IDataStore;
import com.yukthi.persistence.ITransaction;
import com.yukthi.persistence.JoinTableDetails;
import com.yukthi.persistence.JoinTableEntity;
import com.yukthi.persistence.Record;
import com.yukthi.persistence.conversion.ConversionService;
import com.yukthi.persistence.listeners.EntityEventType;
import com.yukthi.persistence.query.ColumnParam;
import com.yukthi.persistence.query.FinderQuery;
import com.yukthi.persistence.query.QueryCondition;
import com.yukthi.persistence.query.QueryResultField;
import com.yukthi.persistence.query.SaveQuery;
import com.yukthi.persistence.repository.InvalidRepositoryException;
import com.yukthi.persistence.repository.annotations.JoinOperator;
import com.yukthi.persistence.repository.annotations.Operator;
import com.yukthi.utils.ObjectWrapper;
import com.yukthi.utils.exceptions.InvalidStateException;

@QueryExecutorPattern(prefixes = {"save"})
public class SaveQueryExecutor extends AbstractPersistQueryExecutor
{
	private static Logger logger = LogManager.getLogger(SaveQueryExecutor.class);
	private static final String COL_UQ_ENTITY_ID = "UQ_ENTITY_ID";
	
	private Class<?> returnType;
	
	public SaveQueryExecutor(Class<?> repositoryType, Method method, EntityDetails entityDetails)
	{
		super.entityDetails = entityDetails;
		super.repositoryType = repositoryType;

		Class<?> paramTypes[] = method.getParameterTypes();
		boolean isCoreInterface = ICrudRepository.class.equals(method.getDeclaringClass());

		if(paramTypes.length != 1)
		{
			throw new InvalidRepositoryException("Non-single parameter save method '" + method.getName() + "' in repository: " + repositoryType.getName());
		}
		
		if(!entityDetails.getEntityType().equals(paramTypes[0]) && !isCoreInterface)
		{
			throw new InvalidRepositoryException("Save method '" + method.getName() + "' found with non-entity parameter in repository: " + repositoryType.getName());
		}
		
		returnType = method.getReturnType();
		
		if(!boolean.class.equals(returnType) && !void.class.equals(returnType))
		{
			throw new InvalidRepositoryException("Save method '" + method.getName() + "' found with non-boolean and non-void return type in repository: " + repositoryType.getName());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.persistence.repository.executors.QueryExecutor#execute(com.yukthi.persistence.repository.executors.QueryExecutionContext, com.yukthi.persistence.IDataStore, com.yukthi.persistence.conversion.ConversionService, java.lang.Object[])
	 */
	@Override
	public Object execute(QueryExecutionContext context, IDataStore dataStore, ConversionService conversionService, Object... params)
	{
		logger.trace("Started method: execute");
		
		Object entity = params[0];

		if(entity == null)
		{
			throw new NullPointerException("Entity can not be null");
		}
		
		if(dataStore.isExplicitUniqueCheckRequired())
		{
			//check if unique constraints are getting violated
			checkForUniqueConstraints(dataStore, conversionService, entity, false);
		}
		
		if(dataStore.isExplicitForeignCheckRequired())
		{
			//check if all foreign parent keys are available
			checkForForeignConstraints(dataStore, conversionService, entity);
		}

		SaveQuery query = new SaveQuery(entityDetails);
		Object value = null;
		
		//wrapper object to hold id value
		ObjectWrapper<Object> idWrapper = new ObjectWrapper<>();
		ForeignConstraintDetails foreignConstraint = null;
		
		//keep track of the fields that needs to be added after main entity is saved
		Map<FieldDetails, Object> tableJoinedFields = new HashMap<>();
		Map<FieldDetails, Object> childFields = new HashMap<>();
		
		for(FieldDetails field: entityDetails.getFieldDetails())
		{
			if(field.isIdField())
			{
				if(field.getGenerationType() == GenerationType.IDENTITY)
				{
					continue;
				}
				
				if(field.getGenerationType() == GenerationType.SEQUENCE)
				{
					query.addColumn(new ColumnParam(field.getDbColumnName(), null, -1, field.getSequenceName()));
					continue;
				}
			}
			
			//get the value of the field
			value = field.getValue(entity);

			//current field is relation field
			if(field.isRelationField())
			{
				//if value is null ignore current field
				if(value == null)
				{
					 continue;
				}
				
				foreignConstraint = field.getForeignConstraintDetails();
				
				//if the field is part of current table
				if(field.isTableOwned())
				{
					//fetch the value of related entity and store it in this table
					value = foreignConstraint.getTargetEntityDetails().getIdField().getValue(value);
				}
				//if the relation is maintained by using intermediate table
				else if(field.isTableJoined())
				{
					tableJoinedFields.put(field, value);
					continue;
				}
				//if the relation is mapped relation
				else
				{
					//ensure this is mapped relation
					if(foreignConstraint.getMappedBy() == null)
					{
						//this should never be the case. As the relation is not owned by this table and is not table joined
						//		it should be mapped relation by parent entity
						
						throw new IllegalStateException( String.format("Non mapped-by relation encountered when expecting mapped relation - %s.%s", 
								entityDetails.getEntityType().getName(), field.getName()) );
					}
					//if save is not cascaded to child entities
					if(!foreignConstraint.isSaveCascaded())
					{
						logger.trace("Ignoring child entity maintained by field {} as relation is not PERSIST cascaded", field.getName());
						continue;
					}
					
					childFields.put(field, value);
					continue;
				}
			}

			//convert to db data type
			value = conversionService.convertToDBType(value, field);
			
			query.addColumn(new ColumnParam(field.getDbColumnName(), value, -1));
			
			//if field is id field and value was set manually
			if(field.isIdField())
			{
				idWrapper.setValue(value);
			}
		}
		
		//add random unique id while persisting entity, which in turn can be used to fetch primary key value
		String entityUid = UUID.randomUUID().toString();
		query.addColumn(new ColumnParam(COL_UQ_ENTITY_ID, entityUid, -1));
		
		//save the entity
		try(ITransaction transaction = dataStore.getTransactionManager().newOrExistingTransaction())
		{
			super.notifyEntityEvent(null, entity, EntityEventType.PRE_SAVE);

			int res = dataStore.save(query, entityDetails, idWrapper);
			
			//if insertion was successful
			if(res > 0)
			{
				//if id value is found from statement or was explicitly specified
				if(idWrapper.getValue() == null)
				{
					//fetch the newly save entry id and populate it to entity
					idWrapper.setValue( fetchId(entity, dataStore, entityUid, conversionService) );
				}
				else
				{
					//set the id value on entity
					entityDetails.getIdField().setValue(entity, idWrapper.getValue());
				}
				
				saveExtensionFields((Long)idWrapper.getValue(), entity, entityDetails, conversionService, dataStore);
				
				//save child entities, if any
				for(FieldDetails field : childFields.keySet())
				{
					/*
					 * Child fields are fields with mapped relation under current entity.
					 * Saving child entity with inverse relation will take care of populating join 
					 * table update, if required
					 */
					saveChildEntities(field, childFields.get(field), entity);
				}
				
				//save join table entries if any
				for(FieldDetails field : tableJoinedFields.keySet())
				{
					saveJoinTableEntry(field, entity, tableJoinedFields.get(field), conversionService, dataStore);
				}
				
				super.notifyEntityEvent(null, entity, EntityEventType.POST_SAVE);
			}
			
			transaction.commit();
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
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void saveExtensionFields(long id, Object entity, EntityDetails entityDetails, ConversionService conversionService, IDataStore dataStore)
	{
		ExtendedTableDetails extendedTableDetails = entityDetails.getExtendedTableDetails();
		
		if(extendedTableDetails == null)
		{
			return;
		}
		
		Field extFieldsHolder = entityDetails.getExtendedTableDetails().getEntityField();
		Map<String, Object> extendedFields = null;
		
		try
		{
			extFieldsHolder.setAccessible(true);
			extendedFields = (Map) extFieldsHolder.get(entity);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while fetching extension field values");
		}
		
		if(extendedFields == null || extendedFields.isEmpty())
		{
			return;
		}
		
		
		SaveQuery query = new SaveQuery(extendedTableDetails.toEntityDetails(entityDetails));
		query.addColumn(new ColumnParam(ExtendedTableEntity.COLUMN_ENTITY_ID, id, -1));
		
		for(String field : extendedFields.keySet())
		{
			query.addColumn(new ColumnParam(field.toUpperCase(), extendedFields.get(field), -1));
		}

		//save the entity
		int res = dataStore.save(query, entityDetails, new ObjectWrapper<>());

		//if insert failed
		if(res <= 0)
		{
			throw new IllegalStateException("Failed to save extended fields");
		}
	}
	
	/**
	 * Saves entry in join table with specified entity and inverse entity (parent entity)
	 * @param field
	 * @param entity
	 * @param targetEntity
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void saveJoinTableEntry(FieldDetails field, Object entity, Object targetEntity, ConversionService conversionService, IDataStore dataStore)
	{
		JoinTableDetails joinTableDetails = field.getForeignConstraintDetails().getJoinTableDetails();
		EntityDetails joinEntityDetails = joinTableDetails.toEntityDetails();
		
		FieldDetails ownerIdFieldDetails = entityDetails.getIdField();
		FieldDetails targetIdFieldDetails = field.getForeignConstraintDetails().getTargetEntityDetails().getIdField();
		
		//get ids of child and parent
		Object ownerId = ownerIdFieldDetails.getValue(entity);
		Collection<Object> targetEntityList = null;
		
		//if target is collection (ex- many to many relation)
		if(targetEntity instanceof Collection)
		{
			targetEntityList = (Collection)targetEntity;
		}
		else
		{
			targetEntityList = Arrays.asList(targetEntity);
		}
		
		Object targetId = null;
		
		try(ITransaction transaction = dataStore.getTransactionManager().newOrExistingTransaction())
		{
			//loop through the targets
			for(Object target : targetEntityList)
			{
				//fetch target id
				targetId = targetIdFieldDetails.getValue(target);
				
				ownerId = conversionService.convertToDBType(ownerId, joinEntityDetails.getFieldDetailsByField(JoinTableEntity.FIELD_JOIN_COLUMN));
				targetId = conversionService.convertToDBType(targetId, joinEntityDetails.getFieldDetailsByField(JoinTableEntity.FIELD_INV_JOIN_COLUMN));
				
				//build save query
				SaveQuery query = new SaveQuery(joinEntityDetails);
				query.addColumn(new ColumnParam(joinTableDetails.getJoinColumn(), ownerId, -1));
				query.addColumn(new ColumnParam(joinTableDetails.getInverseJoinColumn(), targetId, -1));
		
				//save the entity
				int res = dataStore.save(query, entityDetails, new ObjectWrapper<>());
	
				//if insert failed
				if(res <= 0)
				{
					throw new IllegalStateException("Failed to save join table entry");
				}
			}
			
			transaction.commit();
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
	
	/**
	 * Saves the child entities represented by "value" using specified field details relation
	 * @param field
	 * @param value
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void saveChildEntities(FieldDetails field, Object value, Object parentEntity)
	{
		//get child entity details
		ForeignConstraintDetails foreignConstraint = field.getForeignConstraintDetails();
		
		//fetch child entity details
		EntityDetails childEntityDetails = foreignConstraint.getTargetEntityDetails();

		ICrudRepository childRepository = super.getCrudRepository(childEntityDetails.getEntityType());
		
		FieldDetails childFieldDetails = childEntityDetails.getFieldDetailsByField(foreignConstraint.getMappedBy());
		
		//if value is collection of entities
		if(value instanceof Collection)
		{
			Collection<?> childEntities = (Collection<?>)value;
			
			//save all child entities
			for(Object childEntity : childEntities)
			{
				//set inverse relation on child to parent
				childFieldDetails.setValue(childEntity, parentEntity);
				
				childRepository.save(childEntity);
			}
		}
		else
		{
			//set inverse relation on child to parent
			childEntityDetails.getFieldDetailsByField(foreignConstraint.getMappedBy()).setValue(value, parentEntity);

			childRepository.save(value);
		}
	}
	
	/**
	 * Fetches entity id based on specified uuid (generated for entity)
	 * @param entity
	 * @param dataStore
	 * @param uuid
	 * @param conversionService
	 * @return
	 */
	protected Object fetchId(Object entity, IDataStore dataStore, String uuid, ConversionService conversionService)
	{
		logger.trace("Started method: fetchId");
		
		FieldDetails idFieldDetails = entityDetails.getIdField();
		
		//build finder query
		FinderQuery findQuery = new FinderQuery(entityDetails);
		findQuery.addResultField(new QueryResultField(null, idFieldDetails.getDbColumnName(), null));
		
		findQuery.addCondition(new QueryCondition(null, COL_UQ_ENTITY_ID, Operator.EQ, uuid, JoinOperator.AND, false));
		
		//execute finder query 
		List<Record> records = dataStore.executeFinder(findQuery, entityDetails, null);
		
		//if no records are found return null
		if(records == null || records.isEmpty())
		{
			return null;
		}
		
		//set the id value on entity and return it
		Object idValue = conversionService.convertToJavaType(records.get(0).getObject(0), idFieldDetails);
		idFieldDetails.setValue(entity, idValue);
		
		logger.debug("Got id - {}", idValue);
		
		return idValue;
	}

}
