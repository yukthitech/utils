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
package com.yukthitech.persistence.repository.executors.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.ExtendedTableDetails;
import com.yukthitech.persistence.ExtendedTableEntity;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.ForeignConstraintDetails;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.InvalidMappingException;
import com.yukthitech.persistence.JoinTableDetails;
import com.yukthitech.persistence.Record;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.query.FinderQuery;
import com.yukthitech.persistence.query.IConditionalQuery;
import com.yukthitech.persistence.query.IOrderedQuery;
import com.yukthitech.persistence.query.QueryCondition;
import com.yukthitech.persistence.query.QueryJoinCondition;
import com.yukthitech.persistence.query.QueryResultField;
import com.yukthitech.persistence.query.Subquery;
import com.yukthitech.persistence.repository.PersistenceExecutionContext;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.OrderByType;
import com.yukthitech.persistence.repository.executors.QueryExecutionContext;
import com.yukthitech.persistence.repository.executors.proxy.ProxyEntity;
import com.yukthitech.persistence.repository.executors.proxy.ProxyResultObject;
import com.yukthitech.persistence.repository.search.DynamicResultField;
import com.yukthitech.persistence.repository.search.IDynamicSearchResult;
import com.yukthitech.persistence.repository.search.SearchQuery;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Builder that can be used to keep track of conditions/columns involved and
 * their dependencies. And finally build the required query.
 * 
 * @author akiran
 */
public class ConditionQueryBuilder implements Cloneable
{
	private static final String ID_FIELD_CODE = "RID_0";
	
	/**
	 * Table information required by the query
	 * 
	 * @author akiran
	 */
	private static class TableInfo
	{
		/**
		 * Short code for table
		 */
		private String tableCode;

		/**
		 * Table name
		 */
		private String tableName;

		/**
		 * Entity details representing this table
		 */
		private EntityDetails entityDetails;

		/**
		 * Table code to which this table has to join (in order to get joined
		 * with main table)
		 */
		private String joinTableCode;

		/**
		 * Column to be used in current table for join
		 */
		private String joinTableColumn;

		/**
		 * Column to be used in target table for join
		 */
		private String column;
		
		/**
		 * Indicates if this relation is nullable
		 */
		private boolean nullable;

		public TableInfo(String tableCode, String tableName, EntityDetails entityDetails, String joinTableCode, String sourceColumn, String targetColumn, boolean nullable)
		{
			this.tableCode = tableCode;
			this.tableName = tableName;
			this.entityDetails = entityDetails;
			this.joinTableCode = joinTableCode;
			this.joinTableColumn = sourceColumn;
			this.column = targetColumn;
			this.nullable = nullable;
		}
	}
	
	public static interface ICondition
	{
		public void addCondition(Object condition);
		
		public List<Object> getGroupedConditions();
	}

	/**
	 * Condition details to be used in the query
	 * 
	 * @author akiran
	 */
	public static class Condition implements ICondition
	{
		/**
		 * Condition operator
		 */
		Operator operator;

		/**
		 * Index of the parameter defining this condition
		 */
		int index;

		/**
		 * Embedded property name to be used when condition object in use
		 */
		String embeddedProperty;

		/**
		 * Entity field expression
		 */
		String fieldExpression;

		/**
		 * Table on which this condition needs to be applied
		 */
		TableInfo table;

		/**
		 * Field details (with column name) that can be used for condition
		 */
		FieldDetails fieldDetails;

		/**
		 * Condition join operator
		 */
		JoinOperator joinOperator;
		
		/**
		 * Indicates the condition should be case insensitive
		 */
		boolean ignoreCase;

		/**
		 * Conditions grouped with current condition
		 */
		List<Object> groupedConditions;
		
		boolean nullable;
		
		String defaultValue;
		
		boolean joiningField;
		
		public Condition(Operator operator, int index, String embeddedProperty, String fieldExpression, JoinOperator joinOperator, boolean  nullable, boolean ignoreCase)
		{
			this.operator = operator;
			this.index = index;
			this.embeddedProperty = embeddedProperty;
			this.fieldExpression = fieldExpression;
			this.joinOperator = joinOperator;
			this.nullable = nullable;
			this.ignoreCase = ignoreCase;
		}

		/**
		 * Converts the condition into bean expression that can be used to fetch
		 * condition value from method parameters
		 * 
		 * @return
		 */
		public String getConditionExpression()
		{
			StringBuilder builder = new StringBuilder("parameters[").append(index).append("]");

			if(embeddedProperty != null)
			{
				builder.append(".").append(embeddedProperty);
			}

			return builder.toString();
		}

		/**
		 * Adds value to {@link #groupedConditions groupedConditions}
		 *
		 * @param condition
		 *            condition to be added
		 */
		public void addCondition(Object condition)
		{
			if(groupedConditions == null)
			{
				groupedConditions = new ArrayList<>();
			}

			groupedConditions.add(condition);
		}
		
		@Override
		public List<Object> getGroupedConditions()
		{
			return groupedConditions;
		}
	}

	/**
	 * Represents inner query condition.
	 * @author akiran
	 *
	 */
	public static class InnerQuery implements ICondition
	{
		/**
		 * Current field table to which sub query result should be compared.
		 */
		FieldDetails currentTableField;
		
		/**
		 * Sub query field which needs to be fetched from subquery.
		 */
		FieldDetails subqueryField;
		
		/**
		 * Condition query builder for building sub query.
		 */
		ConditionQueryBuilder subqueryBuilder;
		
		JoinOperator joinOperator;
		
		/**
		 * Conditions grouped with current condition
		 */
		List<Object> groupedConditions;

		public InnerQuery(FieldDetails currentTableField, FieldDetails subqueryField, EntityDetails subqueryEntity, JoinOperator joinOperator, ConditionQueryBuilder conditionQueryBuilder)
		{
			this.currentTableField = currentTableField;
			this.subqueryField = subqueryField;
			
			this.subqueryBuilder = new ConditionQueryBuilder(subqueryEntity, conditionQueryBuilder.nextTableCode(subqueryEntity), conditionQueryBuilder.nextFieldCode(), conditionQueryBuilder.nextTableId);
			this.joinOperator = joinOperator;
		}
		
		/**
		 * Gets the condition query builder for building sub query.
		 *
		 * @return the condition query builder for building sub query
		 */
		public ConditionQueryBuilder getSubqueryBuilder()
		{
			return subqueryBuilder;
		}

		public void addCondition(Object condition)
		{
			if(groupedConditions == null)
			{
				groupedConditions = new ArrayList<>();
			}

			groupedConditions.add(condition);
		}
		
		@Override
		public List<Object> getGroupedConditions()
		{
			return groupedConditions;
		}
	}
	
	/**
	 * Result field details that is part current query
	 * 
	 * @author akiran
	 */
	private static class ResultField
	{
		/**
		 * Result property to which resultant value should be populated
		 */
		private String property;

		/**
		 * Short code for result field
		 */
		private String code;

		/**
		 * Table from which value can be fetched
		 */
		private TableInfo table;

		/**
		 * Field details (with column) from which value can be fetched
		 */
		private FieldDetails fieldDetails;

		/**
		 * Type of result field
		 */
		private Class<?> fieldType;
		
		private OrderByType orderType;

		public ResultField(String property, String code, Class<?> fieldType)
		{
			this.property = property;
			this.code = code;
			this.fieldType = fieldType;
		}
	}

	/**
	 * Bean context that can be used to parse/process expressions
	 * 
	 * @author akiran
	 */
	public static class ParameterContext
	{
		private Object parameters[];
		private QueryExecutionContext queryExecutionContext;

		public ParameterContext(Object[] parameters, QueryExecutionContext queryExecutionContext)
		{
			this.parameters = parameters;
			this.queryExecutionContext = queryExecutionContext;
		}

		public Object[] getParameters()
		{
			return parameters;
		}
	}

	/**
	 * Entity details on which this query is going to be executed
	 */
	private EntityDetails entityDetails;

	/**
	 * List of conditions of this query
	 */
	private List<Object> conditions = new ArrayList<>();

	/**
	 * List of result fields of this query
	 */
	private List<ResultField> resultFields = new ArrayList<>();

	/**
	 * Mapping from entity field name to result field
	 */
	private Map<String, ResultField> fieldToResultField = new HashMap<>();
	
	/**
	 * Mapping from property name to table
	 */
	private Map<String, TableInfo> propToTable = new HashMap<>();

	/**
	 * Mapping from table-short-code to the table. Which will be needed while
	 * giving join info
	 */
	private Map<String, TableInfo> codeToTable = new HashMap<>();

	private List<ResultField> orderByFields = new ArrayList<>();

	/**
	 * Counter for generating unique table codes
	 */
	private AtomicInteger nextTableId;

	/**
	 * Counter for generating unique field codes
	 */
	private int nextFieldId = 1;

	/**
	 * Indicates whether the expected result is single field and is direct
	 * return type (not a bean and sub property)
	 */
	private boolean isSingleFieldReturn;

	private String defTableCode;
	private String defTableIdCol;
	
	/**
	 * Index of parameter specifying limit on number of rows
	 * to be fetched.
	 */
	private Integer limitRowParameterIndex;
	
	/**
	 * Flag indicating if the expected output results have any relation fields or not.
	 */
	private boolean containsRelationResults = false;

	public ConditionQueryBuilder(EntityDetails entityDetails)
	{
		this(entityDetails, "T_" + entityDetails.getShortName(), "T0_ID", new AtomicInteger(1));
	}

	private ConditionQueryBuilder(EntityDetails entityDetails, String defTableCode, String defTableIdCol, AtomicInteger nextTableId)
	{
		this.defTableCode = defTableCode;
		this.defTableIdCol = defTableIdCol;
		
		this.entityDetails = entityDetails;
		this.nextTableId = nextTableId;
		codeToTable.put(defTableCode, new TableInfo(defTableCode, entityDetails.getTableName(), entityDetails, null, null, null, false));
	}
	
	public void setLimitRowParameterIndex(String methodDesc, Integer limitRowParameterIndex)
	{
		if(this.limitRowParameterIndex != null)
		{
			throw new InvalidConfigurationException("In method {} multiple parameters are marked as limit-row param. [Indexes: {}, {}]", 
					methodDesc, this.limitRowParameterIndex, limitRowParameterIndex);
		}
		
		this.limitRowParameterIndex = limitRowParameterIndex;
	}

	/**
	 * Generates new unique table code
	 * 
	 * @return
	 */
	private String nextTableCode(EntityDetails entityDetails)
	{
		int nextId = nextTableId.getAndIncrement();
		String name = "T_" + entityDetails.getShortName();
		
		if(!codeToTable.containsKey(name))
		{
			return name;
		}
		
		return name + nextId;
	}

	/**
	 * Generates new unique field code
	 * 
	 * @return
	 */
	private String nextFieldCode()
	{
		return "R" + (nextFieldId++);
	}

	/**
	 * Creates new table info with specified details and adds it to
	 * {@link #codeToTable} and {@link #propToTable} maps.
	 * 
	 * @param tableCode
	 * @param entityDetails
	 * @param joinTableCode
	 * @param joinTableColumn
	 * @param targetColumn
	 * @param property
	 * @param nullable Indicates if this relation is optional
	 * @return
	 */
	private TableInfo newTableInfo(EntityDetails entityDetails, String tableName, String joinTableCode, String joinTableColumn, String targetColumn, String property, boolean nullable)
	{
		TableInfo newTableInfo = new TableInfo(nextTableCode(entityDetails), tableName, entityDetails, joinTableCode, joinTableColumn, targetColumn, nullable);

		// add new table info to maps

		// property will be null for join tables
		if(property != null)
		{
			propToTable.put(property, newTableInfo);
		}

		codeToTable.put(newTableInfo.tableCode, newTableInfo);

		return newTableInfo;
	}

	private TableInfo getTableInfo(EntityDetails entityDetails, String entityFieldPath[], String paramType, String conditionExpr, String methodDesc, ObjectWrapper<FieldDetails> fieldDetailsWrapper)
	{
		EntityDetails currentEntityDetails = this.entityDetails, targetEntityDetails = null;
		String currentProp = null;
		TableInfo currentTableInfo = codeToTable.get(defTableCode), newTableInfo = null, joinTableInfo = null;
		FieldDetails fieldDetails = null, targetFieldDetails = null;
		int maxIndex = entityFieldPath.length - 1;
		ForeignConstraintDetails foreignConstraint = null, targetConstraint = null;
		JoinTableDetails joinTableDetails = null;
		
		//flag indicating if the relation is became nullable in middle
		boolean nullableRelation = false;

		// loop through field parts and find the required table joins
		for(int i = 0; i < entityFieldPath.length; i++)
		{
			currentProp = (currentProp != null) ? currentProp + "." + entityFieldPath[i] : entityFieldPath[i];
			fieldDetails = currentEntityDetails.getFieldDetailsByField(entityFieldPath[i]);

			// if invalid field details encountered
			if(fieldDetails == null)
			{
				ExtendedTableDetails extendedTableDetails = currentEntityDetails.getExtendedTableDetails();

				//if current field represent extended field
				if(extendedTableDetails != null && extendedTableDetails.getEntityField().getName().equals(entityFieldPath[i]))
				{
					//if extended field is used in middle
					if(i != (maxIndex - 1))
					{
						throw new InvalidMappingException(String.format("Invalid field mapping '%s' found in %s parameter '%s' of %s. "
							+ "Extension field is used in middle/start of expression.", currentProp, paramType, conditionExpr, methodDesc));
					}
					
					EntityDetails extendedEntityDetails = extendedTableDetails.toEntityDetails(currentEntityDetails);
					
					//ensure valid extension field is specified
					if(extendedEntityDetails.getFieldDetailsByField(entityFieldPath[i + 1]) == null)
					{
						throw new InvalidMappingException(String.format("Invalid field mapping '%s.%s' found in %s parameter '%s' of %s. "
								+ "Invalid extension field name specified - %s.", currentProp, entityFieldPath[i], paramType, conditionExpr, methodDesc, entityFieldPath[i]));
					}
					
					newTableInfo = propToTable.get(currentProp);
					
					if(newTableInfo == null)
					{
						newTableInfo = newTableInfo(extendedEntityDetails, extendedEntityDetails.getTableName(), currentTableInfo.tableCode, 
								currentEntityDetails.getIdField().getDbColumnName(), ExtendedTableEntity.COLUMN_ENTITY_ID, currentProp, true);
					}
					
					fieldDetailsWrapper.setValue(extendedEntityDetails.getFieldDetailsByField(entityFieldPath[i + 1]));
					return newTableInfo;
				}
				else
				{
					throw new InvalidMappingException(String.format("Invalid field mapping '%s' found in %s parameter '%s' of %s", currentProp, paramType, conditionExpr, methodDesc));
				}
			}

			// if end of field expression is reached
			if(i == maxIndex)
			{
				// if end field is found to be entity instead of simple property
				if(fieldDetails.isRelationField())
				{
					throw new InvalidMappingException(String.format("Non-simple field mapping '%s' found as %s parameter '%s' of %s", currentProp, paramType, conditionExpr, methodDesc));
				}

				fieldDetailsWrapper.setValue(fieldDetails);

				return currentTableInfo;
			}

			newTableInfo = propToTable.get(currentProp);

			// if table is already found for current property
			if(newTableInfo != null)
			{
				currentTableInfo = newTableInfo;
				currentEntityDetails = newTableInfo.entityDetails;
				continue;
			}

			if(!fieldDetails.isRelationField())
			{
				throw new InvalidMappingException(String.format("Non-relational field mapping '%s' found in %s parameter '%s' of %s", currentProp, paramType, conditionExpr, methodDesc));
			}

			foreignConstraint = fieldDetails.getForeignConstraintDetails();
			targetEntityDetails = foreignConstraint.getTargetEntityDetails();
			
			//in the path if any field becomes nullable, all the joins thereafter should become nullable
			if(fieldDetails.isNullable())
			{
				nullableRelation = true;
			}

			// if this is mapped relation
			if(foreignConstraint.isMappedRelation())
			{
				targetFieldDetails = targetEntityDetails.getFieldDetailsByField(foreignConstraint.getMappedBy());
				targetConstraint = targetFieldDetails.getForeignConstraintDetails();
				joinTableDetails = targetConstraint.getJoinTableDetails();

				// if there is no join table in between
				if(joinTableDetails == null)
				{
					// add target table info
					newTableInfo = newTableInfo(targetEntityDetails, targetEntityDetails.getTableName(), currentTableInfo.tableCode, 
							currentEntityDetails.getIdField().getDbColumnName(), targetFieldDetails.getDbColumnName(), currentProp, nullableRelation);
				}
				// if table was joined via join talbe
				else
				{
					// add join table info
					joinTableInfo = newTableInfo(joinTableDetails.toEntityDetails(), joinTableDetails.getTableName(), currentTableInfo.tableCode, currentEntityDetails.getIdField().getDbColumnName(), 
							joinTableDetails.getInverseJoinColumn(), null, nullableRelation);

					// add target table info
					newTableInfo = newTableInfo(targetEntityDetails, targetEntityDetails.getTableName(), joinTableInfo.tableCode, 
							joinTableDetails.getJoinColumn(), targetEntityDetails.getIdField().getDbColumnName(), currentProp, nullableRelation);
				}
			}
			// if the relation is via join table
			else if(foreignConstraint.getJoinTableDetails() != null)
			{
				joinTableDetails = foreignConstraint.getJoinTableDetails();

				// add join table info
				joinTableInfo = newTableInfo(joinTableDetails.toEntityDetails(), joinTableDetails.getTableName(), currentTableInfo.tableCode, currentEntityDetails.getIdField().getDbColumnName(), 
						joinTableDetails.getJoinColumn(), null, nullableRelation);

				// add target table info
				newTableInfo = newTableInfo(targetEntityDetails, targetEntityDetails.getTableName(), joinTableInfo.tableCode, 
						joinTableDetails.getInverseJoinColumn(), targetEntityDetails.getIdField().getDbColumnName(), currentProp, nullableRelation);
			}
			// if the relation is simple relation
			else
			{
				newTableInfo = newTableInfo(targetEntityDetails, targetEntityDetails.getTableName(), currentTableInfo.tableCode, 
						fieldDetails.getDbColumnName(), targetEntityDetails.getIdField().getDbColumnName(), currentProp, nullableRelation);
			}

			currentTableInfo = newTableInfo;
			currentEntityDetails = targetEntityDetails;
		}

		// only to satisfy compiler
		return null;
	}
	
	public boolean isJoiningField(String entityFieldExpression)
	{
		String entityFieldParts[] = entityFieldExpression.trim().split("\\s*\\.\\s*");
		
		if(entityFieldParts.length != 2)
		{
			return false;
		}
		
		FieldDetails fieldDetails = entityDetails.getFieldDetailsByField(entityFieldParts[0]);
		
		if(fieldDetails == null)
		{
			return false;
		}
		
		if(!fieldDetails.isRelationField())
		{
			return false;
		}
		
		if(!fieldDetails.isTableOwned())
		{
			return false;
		}
		
		EntityDetails targetEntityDetails = fieldDetails.getForeignConstraintDetails().getTargetEntityDetails();
		return targetEntityDetails.getIdField().getName().equals(entityFieldParts[1]);
	}

	private boolean isSubqueryRequired(String entityFieldExpression)
	{
		String entityFieldParts[] = entityFieldExpression.trim().split("\\s*\\.\\s*");
		
		if(entityFieldParts.length < 2)
		{
			return false;
		}
		
		FieldDetails fieldDetails = entityDetails.getFieldDetailsByField(entityFieldParts[0]);
		
		if(fieldDetails == null)
		{
			return false;
		}
		
		if(!fieldDetails.isRelationField())
		{
			return false;
		}
		
		if(entityFieldParts.length == 2)
		{
			EntityDetails targetEntityDetails = fieldDetails.getForeignConstraintDetails().getTargetEntityDetails();
			return ! targetEntityDetails.getIdField().getName().equals(entityFieldParts[1]);
		}
		
		return true;
	}

	/**
	 * Adds the condition with specified details to this builder. This method
	 * evaluates the required table joins if this condition needs to be included
	 * in the final query
	 * 
	 * @param groupHead
	 *            Group head condition to which new condition needs to be added
	 * @param operator
	 * @param index Parameter index at which value for this condition can be found
	 * @param embeddedProperty
	 * @param entityFieldExpression
	 * @param methodDesc
	 * @param nullable Indicates whether condition can hold null values
	 * @return Returns newly added condition which in turn can be used to add
	 *         group conditions
	 */
	public Condition addCondition(ICondition groupHead, Operator operator, int index, String embeddedProperty, 
			String entityFieldExpression, JoinOperator joinOperator, String methodDesc, boolean nullable, boolean ignoreCase, 
			String defaultValue)
	{
		// split the entity field expression
		String entityFieldParts[] = entityFieldExpression.trim().split("\\s*\\.\\s*");

		Condition condition = new Condition(operator, index, embeddedProperty, entityFieldExpression, joinOperator, nullable, ignoreCase);
		condition.joiningField = isJoiningField(entityFieldExpression);
		
		// if this mapping is for direct property mapping
		if(entityFieldParts.length == 1 || condition.joiningField)
		{
			condition.fieldDetails = entityDetails.getFieldDetailsByField(entityFieldParts[0]);

			// if the field mapping is wrong
			if(condition.fieldDetails == null)
			{
				throw new InvalidMappingException(String.format("Invalid field mapping '%1s' found in condition parameter '%2s' of %3s", entityFieldExpression, condition.getConditionExpression(), methodDesc));
			}

			condition.table = codeToTable.get(defTableCode);
			
			if(groupHead != null)
			{
				groupHead.addCondition(condition);
			}
			else
			{
				conditions.add(condition);
			}

			//for method level conditions parse and consider default value
			if(index < 0 && defaultValue != null)
			{
				try
				{
					//set default value as a string on condition. This string will be evaluated 
					//   to a value during query execution. And will evaluate expressions in string, if any at that time.
					condition.defaultValue = defaultValue;
				}catch(Exception ex)
				{
					throw new InvalidMappingException(String.format("Invalid default value specified for field %s for repository method %s", entityFieldExpression, methodDesc), ex);					
				}
			}
			
			return condition;
		}

		// if the mapping is for nested entity field (with foreign key
		// relationships)
		ObjectWrapper<FieldDetails> fieldDetailsHolder = new ObjectWrapper<>();
		TableInfo tableInfo = getTableInfo(entityDetails, entityFieldParts, "condition", condition.getConditionExpression(), methodDesc, fieldDetailsHolder);
		condition.table = tableInfo;
		condition.fieldDetails = fieldDetailsHolder.getValue();

		if(groupHead != null)
		{
			groupHead.addCondition(condition);
		}
		else
		{
			conditions.add(condition);
		}
		
		//for method level conditions parse consider default value
		if(index < 0 && defaultValue != null)
		{
			try
			{
				//set default value as a string on condition. This string will be evaluated 
				//   to a value during query execution. And will evaluate expressions in string, if any at that time.
				condition.defaultValue = defaultValue;
			}catch(Exception ex)
			{
				throw new InvalidMappingException(String.format("Invalid default value specified for field %s for repository method %s", entityFieldExpression, methodDesc), ex);					
			}
		}

		return condition;
	}
	
	public InnerQuery addFieldSubquery(ICondition groupHead, Operator operator, int index, String embeddedProperty, 
			String entityFieldExpression, JoinOperator joinOperator, String methodDesc, boolean nullable, boolean ignoreCase, 
			String defaultValue)
	{
		if(!isSubqueryRequired(entityFieldExpression))
		{
			throw new InvalidArgumentException("Specified expression does not need sub query - {}", entityFieldExpression);
		}
		
		// split the entity field expression
		String entityFieldParts[] = entityFieldExpression.trim().split("\\s*\\.\\s*");

		FieldDetails fieldDetails = entityDetails.getFieldDetailsByField(entityFieldParts[0]);
		EntityDetails targetEntityDetails = fieldDetails.getForeignConstraintDetails().getTargetEntityDetails();

		InnerQuery innerQuery = null;
		
		//if the relation is not owned by current entity
		if(fieldDetails.isMappedRelationField())
		{
			//TODO: Take care when join table is involved
			String ownerField = fieldDetails.getForeignConstraintDetails().getOwnerField().getName();
			innerQuery = new InnerQuery(entityDetails.getIdField(), targetEntityDetails.getFieldDetailsByField(ownerField), targetEntityDetails, joinOperator, this);
		}
		//if the relation is owner by current entity
		else
		{
			innerQuery = new InnerQuery(fieldDetails, targetEntityDetails.getIdField(), targetEntityDetails, joinOperator, this);
		}
		
		//get sub entity property
		int propIdx = entityFieldExpression.indexOf(".");
		String subentityExpr = entityFieldExpression.substring(propIdx + 1, entityFieldExpression.length());
		
		innerQuery.subqueryBuilder.addCondition(null, operator, index, embeddedProperty, subentityExpr, 
				JoinOperator.AND, methodDesc, nullable, ignoreCase, defaultValue);
		
		if(groupHead != null)
		{
			groupHead.addCondition(innerQuery);
		}
		else
		{
			conditions.add(innerQuery);
		}
		
		return innerQuery;
	}

	/**
	 * Adds subquery which is currently used to fetch multi subentities using reverse relation.
	 * 
	 * @param groupHead
	 * @param entityFieldExpression
	 * @param joinOperator
	 * @param methodDesc
	 * @param subquery
	 * @param repositoryFactory
	 * @return
	 */
	public InnerQuery addSubsearchQuery(Condition groupHead, String entityFieldExpression, JoinOperator joinOperator, String methodDesc, 
			SearchQuery subquery, RepositoryFactory repositoryFactory)
	{
		// split the entity field expression
		String entityFieldParts[] = entityFieldExpression.trim().split("\\s*\\.\\s*");
		
		if(entityFieldParts.length > 1)
		{
			throw new IllegalStateException("For subquery currently nested fields are not supported. Method: " + methodDesc);
		}
		
		FieldDetails fieldDetails = entityDetails.getFieldDetailsByField(entityFieldParts[0]);
		EntityDetails targetEntityDetails = repositoryFactory.getRepositoryForEntity(subquery.getSubentityType()).getEntityDetails();
		FieldDetails subfieldDetails = targetEntityDetails.getFieldDetailsByField(subquery.getAdditionalEntityFields().iterator().next());

		InnerQuery innerQuery = new InnerQuery(fieldDetails, subfieldDetails, targetEntityDetails, joinOperator, this);
		
		if(groupHead != null)
		{
			groupHead.addCondition(innerQuery);
		}
		else
		{
			conditions.add(innerQuery);
		}
		
		return innerQuery;
	}

	/**
	 * Used to add condition based on join table directly (without using field expression). This is mainly used to fetch
	 * entities based on parent id (which in turn is mapped in join table).
	 * @param index index at which parent id can be expected
	 * @param groupHead parent condition if any
	 * @param joinOperator join operator to be used
	 * @param methodDesc method description
	 * @param joinTableDetails join table to be used to fetch target entities
	 * @param conditionFieldDetails field to be used in join table condition
	 * @param fetchFieldDetails field to be fetched which in turn will be compared with id of current entity
	 * @return newly built inner query
	 */
	public InnerQuery addJoinTableCondition(int index, Condition groupHead, JoinOperator joinOperator, String methodDesc, 
			EntityDetails joinTableDetails, FieldDetails conditionFieldDetails, FieldDetails fetchFieldDetails)
	{
		InnerQuery innerQuery = new InnerQuery(entityDetails.getIdField(), fetchFieldDetails, joinTableDetails, joinOperator, this);
		
		innerQuery.subqueryBuilder.addResultField(null, Object.class, null, fetchFieldDetails.getName(), methodDesc);
		innerQuery.subqueryBuilder.addCondition(null, Operator.EQ, index, null, conditionFieldDetails.getName(), JoinOperator.AND, methodDesc, false, false, null);
		
		if(groupHead != null)
		{
			groupHead.addCondition(innerQuery);
		}
		else
		{
			conditions.add(innerQuery);
		}
		
		return innerQuery;
	}

	/**
	 * Removes result fields matching with specified property name.
	 * @param propertyName Property name to be removed.
	 */
	public void removeResultField(String propertyName)
	{
		Iterator<ResultField> fieldIt = this.resultFields.iterator();
		ResultField field = null;
		
		while(fieldIt.hasNext())
		{
			field = fieldIt.next();
			
			if(field.property.equals(propertyName))
			{
				fieldIt.remove();
			}
		}
	}
	
	/**
	 * Adds a result field of the query to this builder
	 * 
	 * @param resultProperty
	 * @param entityFieldExpression
	 * @param methodDesc
	 */
	public void addResultField(String resultProperty, Class<?> resultPropertyType, Type resultGenericPropertyType, String entityFieldExpression, String methodDesc)
	{
		// if a field is already added as direct return value
		if(isSingleFieldReturn)
		{
			throw new InvalidMappingException("Encountered second return field addition after setting direct return field");
		}

		/*
		 * resultProperty would be null only when direct field return is expected.
		 * In case if result fields are already added and later direct result is being added as expectation throw error.
		 */
		if(!this.resultFields.isEmpty() && resultProperty == null)
		{
			throw new InvalidMappingException("Encountered addition direct-return field (with null property) after adding a subproperty return field");
		}

		// split the entity field expression
		String entityFieldParts[] = entityFieldExpression.trim().split("\\s*\\.\\s*");
		
		/*
		if(checkAndAddIntermediateQuery(resultProperty, resultPropertyType, resultGenericPropertyType, entityFieldParts, methodDesc))
		{
			return null;
		}
		*/

		ResultField resultField = new ResultField(resultProperty, nextFieldCode(), resultPropertyType);

		// if this mapping is for direct property mapping
		if(entityFieldParts.length == 1)
		{
			resultField.fieldDetails = entityDetails.getFieldDetailsByField(entityFieldParts[0]);

			// if the field mapping is wrong
			if(resultField.fieldDetails == null)
			{
				throw new InvalidMappingException(String.format("Invalid field mapping '%s' found in result parameter '%s' of '%s'", entityFieldExpression, entityFieldExpression, methodDesc));
			}
			
			if(resultField.fieldDetails.isRelationField())
			{
				if(!this.containsRelationResults)
				{
					ResultField idResultField = new ResultField(null, ID_FIELD_CODE, entityDetails.getIdField().getField().getType());
					idResultField.fieldDetails = entityDetails.getIdField();
					idResultField.table = codeToTable.get(defTableCode);
					resultFields.add(idResultField);
					
					this.containsRelationResults = true;
				}
				
				if(!resultField.fieldDetails.isTableOwned())
				{
					return;
				}
			}

			resultField.table = codeToTable.get(defTableCode);
			resultFields.add(resultField);
			fieldToResultField.put(entityFieldExpression, resultField);

			if(resultProperty == null)
			{
				isSingleFieldReturn = true;
			}
			
			return;
		}

		// if the mapping is for nested entity field (with foreign key
		// relationships)
		ObjectWrapper<FieldDetails> fieldDetailsHolder = new ObjectWrapper<>();
		TableInfo tableInfo = getTableInfo(entityDetails, entityFieldParts, "condition", entityFieldExpression, methodDesc, fieldDetailsHolder);
		resultField.table = tableInfo;
		resultField.fieldDetails = fieldDetailsHolder.getValue();

		if(resultField.fieldDetails.isRelationField())
		{
			if(!resultField.fieldDetails.isTableOwned())
			{
				throw new InvalidMappingException(String.format("Invalid field mapping '%s' found in result parameter '%s' of '%s' (non-entity owned subfields are not supported)", 
						entityFieldExpression, entityFieldExpression, methodDesc));
			}
		}
		
		// TODO: If end field represents a collection, check if it can be
		// supported, if not throw exception
		// Note - Reverse mapping property has to be created for such properties

		resultFields.add(resultField);
		fieldToResultField.put(entityFieldExpression, resultField);

		// if this direct return field
		if(resultProperty == null)
		{
			isSingleFieldReturn = true;
		}
	}

	/**
	 * Adds specified table and its dependency tables to the specified
	 * conditional query.
	 * 
	 * @param query
	 * @param tableCode
	 * @param includedTables
	 */
	private void addTables(IConditionalQuery query, String tableCode, Set<String> includedTables)
	{
		String currentCode = tableCode;
		TableInfo tableInfo = null;

		//As the join tables has to come first, the conditions is maintained in reverse order
		List<QueryJoinCondition> reverseOrder = new ArrayList<>(10);
		
		while(currentCode != null)
		{
			// if current table is already added to the query
			if(includedTables.contains(currentCode))
			{
				break;
			}

			tableInfo = codeToTable.get(currentCode);

			//query.addTable(new QueryTable(tableInfo.tableName, currentCode));
			includedTables.add(currentCode);

			if(tableInfo.joinTableCode != null)
			{
				reverseOrder.add(0, new QueryJoinCondition(tableInfo.tableCode, tableInfo.column, tableInfo.joinTableCode, tableInfo.joinTableColumn, tableInfo.tableName, tableInfo.nullable));
			}

			currentCode = tableInfo.joinTableCode;
		}
		
		for(QueryJoinCondition condition : reverseOrder)
		{
			query.addJoinCondition(condition);
		}
	}

	/**
	 * Loads the conditions, tables and fields to the specified conditional
	 * query using specified params
	 * 
	 * @param query
	 * @param params
	 */
	public void loadConditionalQuery(QueryExecutionContext queryExecutionContext, IConditionalQuery query, Object params[])
	{
		query.setDefaultTableCode(defTableCode);
		
		ParameterContext context = new ParameterContext(params, queryExecutionContext);
		loadConditionalQuery(context, query, params);
		
		if(limitRowParameterIndex != null && (query instanceof FinderQuery))
		{
			((FinderQuery) query).setResultsLimit((Integer) params[limitRowParameterIndex]);
		}
	}
	
	private void loadConditionalQuery(ParameterContext context, IConditionalQuery query, Object params[])
	{
		Set<String> includedTables = new HashSet<>();

		// load the result fields to specified query
		for(ResultField field : this.resultFields)
		{
			// add tables and fields to specifies query
			addTables(query, field.table.tableCode, includedTables);
			query.addResultField(new QueryResultField(field.table.tableCode, field.fieldDetails.getDbColumnName(), field.code));
		}

		//note in subqueries where join table is used, id field can be null
		//	so this condition would handle such conditions
		if(codeToTable.get(defTableCode).entityDetails.getIdField() != null)
		{
			query.addResultField(new QueryResultField(defTableCode, codeToTable.get(defTableCode).entityDetails.getIdField().getDbColumnName(), defTableIdCol));
		}

		QueryCondition queryCondition = null;
		
		// load the conditions to the query
		for(Object condition : this.conditions)
		{
			if(condition instanceof Condition)
			{
				queryCondition = buildConditionForQueryFromCondition(context, includedTables, query, (Condition) condition, params);
			}
			else
			{
				queryCondition = buildConditionForQueryFromNested(context, query, (InnerQuery) condition, params, includedTables);
			}
			
			if(queryCondition != null)
			{
				query.addCondition(queryCondition);
			}
		}
	}
	
	/**
	 * Builds conditions recursively that needs to be added to query
	 * @param context Parameter context
	 * @param includedTables Tables included
	 * @param query Query for which conditions are being built
	 * @param condition Condition to be converted
	 * @return Newly built query condition. If value is null, returns null.
	 */
	private QueryCondition buildConditionForQueryFromCondition(ParameterContext context, 
			Set<String> includedTables, IConditionalQuery query, Condition condition, Object params[])
	{
		Object value = null;
				
		// fetch the value for current condition
		try
		{
			//for non-method level conditions fetch the value
			if(condition.index >= 0)
			{
				value = PropertyUtils.getProperty(context, condition.getConditionExpression());
				
				if(condition.fieldDetails != null)
				{
					value = context.queryExecutionContext.getConversionService().convertToDBType(value, condition.fieldDetails);
				}
			}
			//for method level conditions like null-checks and others use default value from condition
			else
			{
				String strValue = condition.defaultValue;
				
				//if no repo context is specified assume empty object
				Object repoContext = context.queryExecutionContext.getRepositoryExecutionContext();
				repoContext = (repoContext != null) ? repoContext : Collections.emptyMap(); 
				
				//if value is present check for expressions and replace them
				if(strValue != null)
				{
					strValue = CommonUtils.replaceExpressions(repoContext, condition.defaultValue, null);
				}
				
				value = ConvertUtils.convert(strValue, condition.fieldDetails.getField().getType());
			}
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while fetching condition value for expression -" + condition.getConditionExpression(), ex);
		}
		
		if(value instanceof Enum)
		{
			value = "" + value;
		}
		
		QueryCondition groupHead = null;

		// if value is not provided, ignore current condition
		if(value != null || (condition.operator.isNullable() && condition.nullable))
		{
			// add tables and conditions to specifies query
			addTables(query, condition.table.tableCode, includedTables);
			
			groupHead = new QueryCondition(condition.table.tableCode, condition.fieldDetails.getDbColumnName(), condition.operator, value, condition.joinOperator, condition.ignoreCase);
			groupHead.setDataType(condition.fieldDetails.getDbDataType());
		}

		//if no group conditions are present on this query
		if(condition.getGroupedConditions() == null)
		{
			return groupHead;
		}
		
		addGroupConditions(groupHead, condition.getGroupedConditions(), context, includedTables, query, params);
		return groupHead;
	}
	
	private QueryCondition addGroupConditions(QueryCondition groupHead, List<Object> groupConditions, ParameterContext context, 
			Set<String> includedTables, IConditionalQuery query, Object params[])
	{
		//if group conditions are present add them recursively to current condition
		QueryCondition grpCondition = null;
		
		for(Object grpInternalCondition : groupConditions)
		{
			if(grpInternalCondition instanceof Condition)
			{
				grpCondition = buildConditionForQueryFromCondition(context, includedTables, query, (Condition) grpInternalCondition, params);
			}
			else
			{
				grpCondition = buildConditionForQueryFromNested(context, query, (InnerQuery) grpInternalCondition, params, includedTables);
			}

			
			if(grpCondition == null)
			{
				continue;
			}
				
			//if initial condition value is null, use first non null condition as group head
			if(groupHead == null)
			{
				groupHead = grpCondition;
			}
			else
			{
				groupHead.addGroupedCondition(grpCondition);
			}
		}
		
		return groupHead;
	}

	private QueryCondition buildConditionForQueryFromNested(ParameterContext paramContext, IConditionalQuery query, InnerQuery innerQuery, Object params[], Set<String> includedTables)
	{
		QueryCondition queryCondition = new QueryCondition(defTableCode, innerQuery.currentTableField.getDbColumnName(), Operator.IN, null, innerQuery.joinOperator, false);
		queryCondition.setSubquery(new Subquery(innerQuery.subqueryBuilder.entityDetails, innerQuery.subqueryBuilder.defTableCode));
		
		innerQuery.subqueryBuilder.loadConditionalQuery(paramContext, queryCondition.getSubquery(), params);
		
		if(innerQuery.getGroupedConditions() == null)
		{
			return queryCondition;
		}
		
		return addGroupConditions(queryCondition, innerQuery.getGroupedConditions(), paramContext, includedTables, query, params);
	}

	/**
	 * Loads the order by fields into specified query.
	 * @param orderedQuery Query to which order by fields should be loaded.
	 */
	public void loadOrderByFields(IOrderedQuery orderedQuery)
	{
		if(this.orderByFields.isEmpty())
		{
			return;
		}

		// loop through order by fields
		for(ResultField field : orderByFields)
		{
			orderedQuery.addOrderByField(new QueryResultField(field.table.tableCode, field.fieldDetails.getDbColumnName(), field.code, field.orderType));
		}
	}

	/**
	 * Parses and converts specified record into specified result type
	 * 
	 * @param record
	 * @param resultType
	 * @param conversionService
	 * @param persistenceExecutionContext
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T parseResult(Record record, Class<T> resultType, ConversionService conversionService, PersistenceExecutionContext persistenceExecutionContext) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException
	{
		if(isSingleFieldReturn)
		{
			ResultField resField = this.resultFields.get(0);
			Object res = conversionService.convertToJavaType(record.getObject(resField.code), resField.fieldDetails);

			return (T) ConvertUtils.convert(res, resField.fieldType);
		}

		T result = resultType.getConstructor().newInstance();
		Object value = null;
		ForeignConstraintDetails foreignConstraint = null;
		EntityDetails foreignEntityDetails = null;

		RepositoryFactory repositoryFactory = persistenceExecutionContext.getRepositoryFactory();
		
		boolean isEntityResultType = (resultType.getAnnotation(Table.class) != null);
		Object idVal = null;

		for(ResultField resultField : this.resultFields)
		{
			value = record.getObject(resultField.code);

			// ignore null values
			if(value == null)
			{
				continue;
			}
			
			if(ID_FIELD_CODE.equals(resultField.code))
			{
				idVal = conversionService.convertToJavaType(value, resultField.fieldDetails);
				idVal = ConvertUtils.convert(idVal, resultField.fieldType);
				continue;
			}

			try
			{
				// as only table owned properties are maintained under
				// returnColumnToField
				// this would be parent (target entity) that needs to be loaded
				if(resultField.fieldDetails.isRelationField())
				{
					foreignConstraint = resultField.fieldDetails.getForeignConstraintDetails();
					foreignEntityDetails = foreignConstraint.getTargetEntityDetails();

					value = ProxyEntity.newProxyById(foreignEntityDetails, repositoryFactory.getRepositoryForEntity((Class) foreignEntityDetails.getEntityType()), value);
				}
				//if this is extension field
				else if(resultField.property.startsWith("@"))
				{
					value = value.toString();
					
					if(resultType.equals(entityDetails.getEntityType()))
					{
						Map<String, Object> customFldMap = (Map)entityDetails.getExtendedTableDetails().getEntityField().get(result);
						
						if(customFldMap == null)
						{
							customFldMap = new HashMap<>();
							entityDetails.getExtendedTableDetails().getEntityField().set(result, customFldMap);
						}
						
						customFldMap.put(resultField.property.substring(1), value);
						continue;
					}
					else
					{
						String entityExtField = entityDetails.getExtendedTableDetails().getEntityField().getName();
						((IDynamicSearchResult)result).addField(new DynamicResultField(entityExtField + "." + resultField.property.substring(1), value));
						continue;
					}
				}
				//if this is additional property field
				else if(resultField.property.startsWith("#"))
				{
					value = conversionService.convertToJavaType(value, resultField.fieldDetails);
					((IDynamicSearchResult)result).addField(new DynamicResultField(resultField.property.substring(1), value));
					continue;
				}
				// if current field is a simple field (non relation field)
				else
				{
					value = conversionService.convertToJavaType(value, resultField.fieldDetails);
					value = ConvertUtils.convert(value, resultField.fieldType);
				}
			} catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while converting field {} value - {}", resultField.fieldDetails.getName(), value, ex);
			}

			// if value is null after conversion, ignore value
			if(value == null)
			{
				continue;
			}

			PropertyUtils.setProperty(result, resultField.property, value);
		}

		//if return type is target entity type, wrap result with proxy to take care of sub relations
		if(isEntityResultType)
		{
			ICrudRepository<?> resultRepo = repositoryFactory.getRepositoryForEntity(resultType);
			result = (T) ProxyEntity.newProxyByEntity(resultRepo.getEntityDetails(), resultRepo, result, Collections.emptyMap());
		}
		else if(containsRelationResults)
		{
			// Id should not be null, when relations are involved
			if(idVal == null)
			{
				throw new InvalidStateException("No id-value found for result object with relations");
			}
			
			ICrudRepository<?> resultRepo = repositoryFactory.getRepositoryForEntity(entityDetails.getEntityType());
			result = (T) ProxyResultObject.newProxy(resultRepo.getEntityDetails(), resultRepo, result, idVal);
		}
		
		return result;
	}

	/**
	 * Converts specified records into specified return type beans
	 * 
	 * @param records
	 * @param returnType
	 * @param resultCollection
	 * @param conversionService
	 * @param persistenceExecutionContext
	 */
	public <T> void parseResults(List<Record> records, Class<T> returnType, Collection<T> resultCollection, ConversionService conversionService, PersistenceExecutionContext persistenceExecutionContext)
	{
		for(Record record : records)
		{
			try
			{
				resultCollection.add(parseResult(record, returnType, conversionService, persistenceExecutionContext));
			} catch(Exception ex)
			{
				throw new IllegalArgumentException("An error occurred while parsing record - " + record, ex);
			}
		}
	}

	/**
	 * Used by search query to clear order by fields before adding dynamic order
	 * by fields
	 */
	public void clearOrderByFields()
	{
		this.orderByFields.clear();
	}

	public void addOrderByField(String field, OrderByType orderByType, String methodDesc)
	{
		ResultField resultField = fieldToResultField.get(field);
		
		if(resultField == null)
		{
			throw new InvalidMappingException("Field '" + field + "' specified in @OrderBy annotation is not part of result list of finder query - " + methodDesc);
		}
		
		if(orderByType == null)
		{
			orderByType = OrderByType.ASC;
		}
			
		resultField.orderType = orderByType;
		this.orderByFields.add(resultField);
	}

	@Override
	public ConditionQueryBuilder clone()
	{
		try
		{
			ConditionQueryBuilder newBuilder = (ConditionQueryBuilder) super.clone();
			newBuilder.codeToTable = new HashMap<>(codeToTable);
			newBuilder.conditions = new ArrayList<>(conditions);
			newBuilder.fieldToResultField = new HashMap<>(fieldToResultField);
			newBuilder.orderByFields = new ArrayList<>(orderByFields);
			newBuilder.propToTable = new HashMap<>(propToTable);
			newBuilder.resultFields = new ArrayList<>(resultFields);

			return newBuilder;
		} catch(CloneNotSupportedException ex)
		{
			throw new IllegalStateException("An error occurred while cloning", ex);
		}
	}
}
