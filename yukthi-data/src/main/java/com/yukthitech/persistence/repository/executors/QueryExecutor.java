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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.IDataFilter;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.listeners.EntityEventType;
import com.yukthitech.persistence.repository.InvalidRepositoryException;
import com.yukthitech.persistence.repository.PersistenceExecutionContext;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.ConditionBean;
import com.yukthitech.persistence.repository.annotations.Conditions;
import com.yukthitech.persistence.repository.annotations.DefaultCondition;
import com.yukthitech.persistence.repository.annotations.ExtendedFieldNames;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.LimitRows;
import com.yukthitech.persistence.repository.annotations.MethodConditions;
import com.yukthitech.persistence.repository.annotations.NullCheck;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.executors.builder.ConditionQueryBuilder;
import com.yukthitech.utils.StringUtils;
import com.yukthitech.utils.annotations.RecursiveAnnotationFactory;

public abstract class QueryExecutor
{
	private static Logger logger = LogManager.getLogger(QueryExecutor.class);

	protected EntityDetails entityDetails;
	protected Class<?> repositoryType;
	
	protected PersistenceExecutionContext persistenceExecutionContext;
	
	protected RecursiveAnnotationFactory recursiveAnnotationFactory = new RecursiveAnnotationFactory();
	
	public void setPersistenceExecutionContext(PersistenceExecutionContext persistenceExecutionContext)
	{
		this.persistenceExecutionContext = persistenceExecutionContext;
	}
	
	protected ICrudRepository<?> getCrudRepository(Class<?> entityType)
	{
		return persistenceExecutionContext.getRepositoryFactory().getRepositoryForEntity(entityType);
	}
	
	/**
	 * Notifies entity listeners, if any, about the specified event via event-listener manager 
	 * @param entity
	 * @param eventType
	 */
	protected void notifyEntityEvent(Object key, Object entity, EntityEventType eventType)
	{
		RepositoryFactory factory = persistenceExecutionContext.getRepositoryFactory();
		factory.getEntityListenerManager().handleEventType(entityDetails.getEntityType(), factory, key, entity, eventType);
	}
	
	/**
	 * Checks if listener is available for specified event
	 * @param eventType
	 * @return
	 */
	protected boolean isListenerAvailable(EntityEventType eventType)
	{
		RepositoryFactory factory = persistenceExecutionContext.getRepositoryFactory();
		return factory.getEntityListenerManager().isListenerPresent(entityDetails.getEntityType(), eventType);
	}
	
	public abstract Object execute(QueryExecutionContext context, IDataStore dataStore, ConversionService conversionService, Object... params);
	
	private ConditionQueryBuilder.ICondition addFieldCondition(Field field, String methodDesc, boolean allowNested, 
			Condition conditionAnnot, ConditionQueryBuilder conditionQueryBuilder, int index, 
			ConditionQueryBuilder.ICondition condGroup, JoinOperator joinOp)
	{
		//fetch entity field name
		String fieldName = conditionAnnot.value();
		
		//if name is not specified in condition
		if(fieldName.trim().length() == 0)
		{
			//use field name
			fieldName = field.getName();
		}
		
		if(!allowNested && fieldName.contains("."))
		{
			throw new InvalidRepositoryException("Nested expression '{}' when plain properties are expected in {}", fieldName, methodDesc);
		}

		boolean ignoreCase = (String.class.equals(field.getType()) && conditionAnnot.ignoreCase());
		joinOp = (joinOp == null) ? conditionAnnot.joinWith() : joinOp;
		
		if(!allowNested && fieldName.contains(".") && !conditionQueryBuilder.isJoiningField(fieldName))
		{
			return conditionQueryBuilder.addFieldSubquery(condGroup, conditionAnnot.op(), index, field.getName(), fieldName.trim(), 
					joinOp, methodDesc, conditionAnnot.nullable(), ignoreCase, null);
		}

		return conditionQueryBuilder.addCondition(condGroup, conditionAnnot.op(), index, field.getName(), fieldName, 
				joinOp, methodDesc, conditionAnnot.nullable(), ignoreCase, null);
	}
	
	private boolean fetchConditionsFromObject(String methodName, Class<?> queryobjType,  
			int index, ConditionQueryBuilder conditionQueryBuilder, String methodDesc, boolean allowNested)
	{
		Field fields[] = queryobjType.getDeclaredFields();
		Condition condition = null;
		Conditions conditions = null;
		boolean found = false;
		
		//loop through query object type fields 
		for(Field field : fields)
		{
			condition = field.getAnnotation(Condition.class);
			conditions = field.getAnnotation(Conditions.class);
			
			if(condition != null)
			{
				addFieldCondition(field, methodDesc, allowNested, condition, conditionQueryBuilder, index, null, null);
			}
			else if(conditions != null)
			{
				ConditionQueryBuilder.ICondition groupCond = null, cond = null;
				
				for(Condition subcond : conditions.value())
				{
					cond = addFieldCondition(field, methodDesc, allowNested, subcond, conditionQueryBuilder, index, 
							groupCond, 
							//for first condition pass conditions join with op, for following condition itself will specify
							(groupCond == null ? conditions.joinWith() : null) 
							);
					
					if(groupCond == null)
					{
						groupCond = cond;
					}
				}
			}
			//if field is not marked as condition
			else
			{
				continue;
			}
			
			found = true;
		}
		
		return found;
	}
	
	protected boolean fetchConditonsByAnnotations(Method method, 
			boolean expectAllConditions, ConditionQueryBuilder conditionQueryBuilder, String methodDesc, boolean allowNested)
	{
		logger.trace("Started method: fetchConditonsByAnnotations");

		Parameter parameters[] = method.getParameters();
		
		ConditionBean conditionBean = null;
		Condition condition = null;
		Conditions conditions = null;
		LimitRows limitRows = null;
		boolean found = false;
		
		//fetch conditions for each argument
		for(int i = 0; i < parameters.length; i++)
		{
			condition = parameters[i].getAnnotation(Condition.class);
			conditions = parameters[i].getAnnotation(Conditions.class);
			limitRows = parameters[i].getAnnotation(LimitRows.class);
			
			if(limitRows != null)
			{
				conditionQueryBuilder.setLimitRowParameterIndex(methodDesc, i);
				continue;
			}
			
			//if condition is not found on attr
			if(condition == null && conditions == null)
			{
				//check for query object annotation
				conditionBean = parameters[i].getAnnotation(ConditionBean.class); 
				
				//if query object is found find nested conditions
				if(conditionBean != null)
				{
					if( fetchConditionsFromObject(method.getName(), parameters[i].getType(), i, conditionQueryBuilder, methodDesc, allowNested) )
					{
						found = true;
					}
					
					continue;
				}
				
				if(parameters[i].getAnnotation(ExtendedFieldNames.class) != null)
				{
					continue;
				}
				
				if(!expectAllConditions)
				{
					continue;
				}
				
				//ignore data filter parameters
				if(IDataFilter.class.isAssignableFrom(parameters[i].getType()))
				{
					continue;
				}
				
				if(found)
				{
					throw new InvalidRepositoryException("@Condition/@ConditionBean is not defined for all parameters of method '" 
								+ method.getName() + "' of repository: " + repositoryType.getName());
				}
				
				return false;
			}
			
			if(condition != null)
			{
				addParamCondition(condition, conditionQueryBuilder, methodDesc, method, allowNested, i, null);
			}
			else if(conditions != null)
			{
				ConditionQueryBuilder.ICondition groupCond = null, cond = null;
				
				for(Condition subcond : conditions.value())
				{
					cond = addParamCondition(subcond, conditionQueryBuilder, methodDesc, method, allowNested, i, groupCond);
					
					if(groupCond == null)
					{
						groupCond = cond;
					}
				}
			}
			
			found = true;
		}

		return found;
	}
	
	private ConditionQueryBuilder.ICondition addParamCondition(Condition condition, ConditionQueryBuilder conditionQueryBuilder, String methodDesc, 
			Method method, boolean allowNested, int paramIdx, ConditionQueryBuilder.ICondition groupHead)
	{
		String fieldName = condition.value();
		
		if(fieldName.trim().length() == 0)
		{
			throw new InvalidRepositoryException("No name is specified in @Condition parameter of method '" 
					+ method.getName() + "' of repository: " + repositoryType.getName());
		}

		boolean ignoreCase = condition.ignoreCase();
		
		if(!allowNested && fieldName.contains(".") && !conditionQueryBuilder.isJoiningField(fieldName))
		{
			return conditionQueryBuilder.addFieldSubquery(groupHead, condition.op(), paramIdx, null, 
					fieldName.trim(), condition.joinWith(), methodDesc, condition.nullable(), ignoreCase, null);
		}
		
		return conditionQueryBuilder.addCondition(groupHead, condition.op(), paramIdx, null, fieldName.trim(), 
				condition.joinWith(), methodDesc, condition.nullable(), ignoreCase, null);
	}
	
	protected boolean fetchConditionsByName(Method method, ConditionQueryBuilder conditionQueryBuilder, String methodDesc)
	{
		logger.trace("Started method: fetchConditionsByName");
		
		String name = method.getName();
		int idx = name.indexOf("By");
		
		if(idx < 0 || (idx + 2) >= name.length())
		{
			return false;
		}
		
		name = name.substring(idx + 2);
		String fieldNames[] = name.split("And");
		
		if(method.getParameterTypes().length < fieldNames.length)
		{
			throw new InvalidRepositoryException("Unable to find sufficient fields names from " + methodDesc);
		}
		
		int index = 0;
		
		for(String field: fieldNames)
		{
			field = StringUtils.toStartLower(field);
			
			conditionQueryBuilder.addCondition(null, Operator.EQ, index, null, field, JoinOperator.AND, methodDesc, false, false, null);
			index++;
		}
		
		return true;
	}
	
	/**
	 * Checks and adds conditions defined at method level
	 * @param method
	 * @param conditionQueryBuilder
	 * @param methodDesc
	 */
	protected void fetchMethodLevelConditions(Method method, ConditionQueryBuilder conditionQueryBuilder, String methodDesc, boolean allowNested)
	{
		//obtain method level conditions
		List<MethodConditions> methodConditionslst = recursiveAnnotationFactory.findAllAnnotationsRecursively(method, MethodConditions.class); 
		
		if(methodConditionslst == null)
		{
			return;
		}
		
		NullCheck nullChecks[] = null;
		DefaultCondition defConditions[] = null;
		String fieldName = null;
		
		for(MethodConditions methodConditions : methodConditionslst)
		{
			nullChecks = methodConditions.nullChecks();
			
			//check and add null based conditions
			if(nullChecks != null)
			{
				Operator operator = null;
				
				for(NullCheck check : nullChecks)
				{
					operator = check.checkForNotNull() ? Operator.NE : Operator.EQ;
					fieldName = check.field();
					
					if(!allowNested && fieldName.contains(".") && !conditionQueryBuilder.isJoiningField(fieldName))
					{
						conditionQueryBuilder.addFieldSubquery(null, operator, -1, null, fieldName, check.joinOperator(), methodDesc, true, false, null);
						//throw new InvalidRepositoryException(String.format("Encountered nested expression '%s' when plain properties are expected in %s", fieldName, methodDesc));
					}
					else
					{
						//by specifying -1 as parameter index, we are telling that the value will not be provided as part of parameters
						conditionQueryBuilder.addCondition(null, operator, -1, null, fieldName, check.joinOperator(), methodDesc, true, false, null);
					}
				}
			}
			
			defConditions = methodConditions.conditions();
			
			//check and add default conditions
			if(defConditions != null)
			{
				for(DefaultCondition condition : defConditions)
				{
					fieldName = condition.field();
					
					if(!allowNested && fieldName.contains(".") && !conditionQueryBuilder.isJoiningField(fieldName))
					{
						conditionQueryBuilder.addFieldSubquery(null, condition.op(), -1, null, fieldName, condition.joinOperator(), methodDesc, true, false, condition.value());
						//throw new InvalidRepositoryException(String.format("Encountered nested expression '%s' when plain properties are expected in %s", fieldName, methodDesc));
					}
					else
					{
						//by specifying -1 as parameter index, we are telling that the value will not be provided as part of parameters
						conditionQueryBuilder.addCondition(null, condition.op(), -1, null, fieldName, condition.joinOperator(), methodDesc, true, false, condition.value());
					}
				}
			}
		}
	}
}
