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

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.ForeignConstraintDetails;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.IInternalRepository;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.search.SearchCondition;
import com.yukthitech.persistence.utils.OrmUtils;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents proxy for entity class used for lazy loading
 * @author akiran
 */
public class ProxyResultObject
{
	/**
	 * The actual result object. 
	 */
	private Object actualResult;
	
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
	 * Proxy object that will be exposed to outside world
	 */
	private Object proxyEntity;
	
	public static Object newProxy(EntityDetails entityDetails, ICrudRepository<?> repository, Object result, Object entityId)
	{
		ProxyResultObject creator = new ProxyResultObject(entityDetails, repository, entityId, result);
		creator.populateRelationFields();
		
		return creator.proxyEntity;
	}

	/**
	 * Creates a proxy for specified entity type
	 * @param entityDetails
	 * @param repository
	 * @param entityType
	 */
	private ProxyResultObject(EntityDetails entityDetails, ICrudRepository<?> repository, Object entityId, Object result)
	{
		this.repository = repository;
		this.entityId = entityId;
		this.entityDetails = entityDetails;
		this.actualResult = result;
		
		//create ccg lib handler which will handle method calls on proxy
		this.proxyEntity = ProxyBuilder.buildProxy(result.getClass(), IProxyEntity.class, this::invoke);
	}
	
	/**
	 * Populates proxies for all relation fields.
	 */
	private void populateRelationFields()
	{
		EntityDetails entityDetails = repository.getEntityDetails();
		
		OrmUtils.processFields(actualResult.getClass(), field -> 
		{
			Field fieldAnnot = field.getAnnotation(Field.class);
			
			if(fieldAnnot == null)
			{
				return;
			}
			
			String fieldName = fieldAnnot.value();
			fieldName = StringUtils.isBlank(fieldName) ? field.getName() : fieldName;

			FieldDetails fieldDetails = entityDetails.getFieldDetailsByField(fieldName);
			
			//ignore non-relation field
			if(!fieldDetails.isRelationField())
			{
				return;
			}

			//fetch target entity details and repo
			ForeignConstraintDetails foreignConstraintDetails = fieldDetails.getForeignConstraintDetails(); 
			EntityDetails targetEntityDetails = foreignConstraintDetails.getTargetEntityDetails();
			ICrudRepository<?> relatedRepo = ((IInternalRepository) repository).getRepositoryFactory().getRepositoryForEntity(targetEntityDetails.getEntityType());
			
			Object fieldValue = null;

			//if target is multi valued field
			if(Collection.class.isAssignableFrom(fieldDetails.getField().getType()))
			{
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
				
				fieldValue = resCollection;
			}
			//if the target is single valued field
			else if(foreignConstraintDetails.isMappedRelation())
			{
				SearchCondition relationCondition = new SearchCondition(foreignConstraintDetails.getMappedBy(), Operator.EQ, entityId);
				Object relatedEntity = ProxyEntity.newProxyByCondition(targetEntityDetails, relatedRepo, relationCondition);
				
				fieldValue = relatedEntity;
			}
			else
			{
				return;
			}
			
			try
			{
				field.setAccessible(true);
				field.set(actualResult, fieldValue);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while setting result field value", ex);
			}
			
		});
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
		
		//handle proxy entity method
		ObjectWrapper<Object> proxyMethodValue = new ObjectWrapper<Object>();
		
		if(handlProxyMethodInvocation(method, proxyMethodValue))
		{
			return proxyMethodValue.getValue();
		}

		return method.invoke(actualResult, args);
	}
	
}
