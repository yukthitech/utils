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
package com.yukthitech.persistence.repository.executors.proxy;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.ForeignConstraintDetails;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.IInternalRepository;
import com.yukthitech.persistence.NoSuchEntityException;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.search.SearchCondition;
import com.yukthitech.persistence.repository.search.SearchQuery;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.PropertyAccessor;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Represents proxy for entity class used for lazy loading
 * @author akiran
 */
public class ProxyEntity
{
	/**
	 * The actual entity which would be loaded lazily on need basis 
	 */
	private Object actualEntity;
	
	/**
	 * Flag indicating if actual entity is loaded
	 */
	private boolean actualEntityLoaded = false;
	
	/**
	 * CRUD repository for the entity
	 */
	private ICrudRepository<?> repository;
	
	/**
	 * Entity details of the current entity.
	 */
	private EntityDetails entityDetails;
	
	/**
	 * Id of the entity
	 */
	private Object entityId;
	
	/**
	 * Id getter method of entity
	 */
	private Method idGetter;
	
	/**
	 * If id is not specified, this condition is used to fetch entity
	 */
	private SearchCondition searchCondition;
	
	/**
	 * Proxy object that will be exposed to outside world
	 */
	private Object proxyEntity;
	
	public static Object newProxyById(EntityDetails entityDetails, ICrudRepository<?> repository, Object entityId)
	{
		if(entityId == null)
		{
			throw new NullPointerException("Entity id can not be null");
		}
		
		ProxyEntity creator = new ProxyEntity(entityDetails, repository, entityId, null);
		return creator.proxyEntity;
	}

	public static Object newProxyByCondition(EntityDetails entityDetails, ICrudRepository<?> repository, SearchCondition condition)
	{
		if(condition == null)
		{
			throw new NullPointerException("Condition can not be null");
		}
		
		ProxyEntity creator = new ProxyEntity(entityDetails, repository, null, condition);
		return creator.proxyEntity;
	}

	public static Object newProxyByEntity(EntityDetails entityDetails, ICrudRepository<?> repository, Object entity, Map<String, Object> dataMap)
	{
		if(!entityDetails.getEntityType().isAssignableFrom(entity.getClass()))
		{
			throw new InvalidArgumentException("Specified entity type '{}' is not matching with specified entity: {}", entityDetails.getEntityType().getName(), entity);
		}
		
		ProxyEntity creator = new ProxyEntity(entityDetails, repository, null, null);
		creator.actualEntity = entity;
		creator.populateRelationFields(dataMap);
		creator.actualEntityLoaded = true;
		
		return creator.proxyEntity;
	}

	/**
	 * Creates a proxy for specified entity type
	 * @param entityDetails
	 * @param repository
	 * @param entityType
	 */
	private ProxyEntity(EntityDetails entityDetails, ICrudRepository<?> repository, Object entityId, SearchCondition condition)
	{
		this.repository = repository;
		this.entityId = entityId;
		this.searchCondition = condition;
		this.entityDetails = entityDetails;
		
		Class<?> entityType = entityDetails.getEntityType();
		
		if(entityId != null)
		{
			//fetch the id getter method
			try
			{
				String idFieldName = entityDetails.getIdField().getName();
				PropertyAccessor.Property property = PropertyAccessor.getProperties(entityType).get(idFieldName);
				
				this.idGetter = property != null ? property.getGetter() : null;
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while fetch id getter for entity type - " + entityType.getName(), ex);
			}
			
			//if unable to find id getter throw error
			if(this.idGetter == null)
			{
				throw new IllegalStateException("Failed to fetch id getter for entity type - " + entityType.getName());
			}
		}
		
		//create ccg lib handler which will handle method calls on proxy
		this.proxyEntity = ProxyBuilder.buildProxy(entityType, IProxyEntity.class, this::invoke);
	}
	
	/**
	 * Populates proxies for all relation fields.
	 */
	private void populateRelationFields(Map<String, Object> flatDataMap)
	{
		EntityDetails entityDetails = repository.getEntityDetails();
		Object value = null;
		
		for(FieldDetails fieldDetails : entityDetails.getFieldDetails())
		{
			//ignore non-relation field
			if(! fieldDetails.isRelationField())
			{
				continue;
			}

			value = fieldDetails.getValue(actualEntity);
			
			//ignore non-null fields
			if(value != null)
			{
				continue;
			}

			//fetch target entity details and repo
			ForeignConstraintDetails foreignConstraintDetails = fieldDetails.getForeignConstraintDetails(); 
			EntityDetails targetEntityDetails = foreignConstraintDetails.getTargetEntityDetails();
			ICrudRepository<?> relatedRepo = ((IInternalRepository) repository).getRepositoryFactory().getRepositoryForEntity(targetEntityDetails.getEntityType());

			//if target is multi valued field
			if(Collection.class.isAssignableFrom(fieldDetails.getField().getType()))
			{
				//if single valued relation is mapped relation (non owned relation)
				Object entityId = entityDetails.getIdField().getValue(actualEntity);
				
				//if for some reason current entity id is missing, set relation also as null
				if(entityId == null)
				{
					continue;
				}
				
				Collection<Object> resCollection = null;
				
				//in case of mapped relation, set the condition to match with current entity id
				if(foreignConstraintDetails.isMappedRelation())
				{
					String mappedField = foreignConstraintDetails.getMappedBy() + "." + foreignConstraintDetails.getTargetEntityDetails().getIdField().getName();
					SearchCondition relationCondition = new SearchCondition(mappedField, Operator.EQ, entityId);
					resCollection = ProxyEntityCollection.newProxyCollectionByCondition(targetEntityDetails, relatedRepo, relationCondition, fieldDetails.getField().getType());
				}
				//when current entity owns the relation via join table (note: for multi valued owned field, join table is indicative)
				else
				{
					resCollection = ProxyJoinEntityCollection.newProxyCollection(entityDetails, repository, fieldDetails.getName(), entityId);
				}
				
				fieldDetails.setValue(actualEntity, resCollection);
			}
			//if the target is single valued field
			else
			{
				//if current table owns the relation
				if(fieldDetails.isTableOwned())
				{
					Object relationId = flatDataMap.get(fieldDetails.getName().toLowerCase());
					
					//if relation id is not found with field name
					if(relationId == null)
					{
						//check if relation id can be found with column name
						relationId = flatDataMap.get( fieldDetails.getDbColumnName().toLowerCase().replace("_", "") );
						
						//if relation id is not found even with column name
						if(relationId == null)
						{
							//ignore current field
							continue;
						}
					}
					
					Object relatedEntity = ProxyEntity.newProxyById(targetEntityDetails, relatedRepo, relationId);
					
					fieldDetails.setValue(actualEntity, relatedEntity);
					continue;
				}

				//if single valued relation is mapped relation (non owned relation)
				Object entityId = entityDetails.getIdField().getValue(actualEntity);
				
				//if for some reason current entity id is missing, set relation also as null
				if(entityId == null)
				{
					continue;
				}
				
				SearchCondition relationCondition = new SearchCondition(foreignConstraintDetails.getMappedBy(), Operator.EQ, entityId);
				Object relatedEntity = ProxyEntity.newProxyByCondition(targetEntityDetails, relatedRepo, relationCondition);
				fieldDetails.setValue(actualEntity, relatedEntity);
			}
		}
	}
	
	/**
	 * Handles proxy method invocation if it is proxy method.
	 * @param method Method being invoked
	 * @param returnValue return value of method invocation
	 * @return true if method is handled.
	 */
	private boolean handlProxyMethodInvocation(Method method, ObjectWrapper<Object> returnValue)
	{
		if(!"$getProxyEntityId".equals(method.getName()))
		{
			return false;
		}
		
		if(entityId != null)
		{
			returnValue.setValue(entityId);
			return true;
		}
		
		if(actualEntity == null)
		{
			return false;
		}
		
		entityId = entityDetails.getIdField().getValue(actualEntity);
		returnValue.setValue(entityId);
		return true;
	}
	
	/**
	 * Proxy method invocation handler method
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	private Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if( Object.class.equals( method.getDeclaringClass()) )
		{
			if("hashCode".equals(method.getName()))
			{
				return entityDetails.getEntityType().hashCode() + entityId.hashCode();
			}
			
			if("equals".equals(method.getName()))
			{
				return proxy == args[0];
			}
			
			if("toString".equals(method.getName()))
			{
				return entityDetails.getEntityType().getName() + "[" + entityId + "]";
			}
			
			return null;
		}
		
		//if the method being invoked is same as id getter simply return the value
		if(idGetter != null && idGetter.equals(method))
		{
			return entityId;
		}
		
		//handle proxy entity method
		ObjectWrapper<Object> proxyMethodValue = new ObjectWrapper<Object>();
		
		if(handlProxyMethodInvocation(method, proxyMethodValue))
		{
			return proxyMethodValue.getValue();
		}

		synchronized(this)
		{
			if(actualEntityLoaded)
			{
				if(actualEntity == null)
				{
					throw new NoSuchEntityException();
				}
				
				return method.invoke(actualEntity, args);
			}
			
			//if enity id is present use it for fetching entity
			if(entityId != null)
			{
				actualEntity = repository.findById(entityId);
			}
			//if search condition is specified to fetch entity
			else if(searchCondition != null)
			{
				@SuppressWarnings({ "unchecked", "rawtypes" })
				List<Object> entities = (List)repository.search(new SearchQuery(this.searchCondition));
				this.actualEntity = (entities.size() > 0) ? entities.get(0) : null;
			}
			
			this.populateRelationFields(Collections.emptyMap());
			actualEntityLoaded = true;
			
			//if entity is not found with specified criteria
			if(actualEntity == null)
			{
				throw new NoSuchEntityException();
			}
			
			//handle proxy entity method, post actual entity is loaded
			if(handlProxyMethodInvocation(method, proxyMethodValue))
			{
				return proxyMethodValue.getValue();
			}

			return method.invoke(actualEntity, args);
		}
	}
	
}
