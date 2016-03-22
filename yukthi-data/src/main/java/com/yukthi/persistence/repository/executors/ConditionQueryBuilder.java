package com.yukthi.persistence.repository.executors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.ExtendedTableDetails;
import com.yukthi.persistence.ExtendedTableEntity;
import com.yukthi.persistence.FieldDetails;
import com.yukthi.persistence.ForeignConstraintDetails;
import com.yukthi.persistence.InvalidMappingException;
import com.yukthi.persistence.JoinTableDetails;
import com.yukthi.persistence.Record;
import com.yukthi.persistence.conversion.ConversionService;
import com.yukthi.persistence.query.FinderQuery;
import com.yukthi.persistence.query.IConditionalQuery;
import com.yukthi.persistence.query.QueryCondition;
import com.yukthi.persistence.query.QueryJoinCondition;
import com.yukthi.persistence.query.QueryResultField;
import com.yukthi.persistence.repository.PersistenceExecutionContext;
import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.persistence.repository.annotations.JoinOperator;
import com.yukthi.persistence.repository.annotations.Operator;
import com.yukthi.persistence.repository.annotations.OrderByType;
import com.yukthi.persistence.repository.executors.proxy.ProxyEntityCreator;
import com.yukthi.persistence.repository.search.DynamicResultField;
import com.yukthi.persistence.repository.search.IDynamicSearchResult;
import com.yukthi.utils.ConvertUtils;
import com.yukthi.utils.ObjectWrapper;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Builder that can be used to keep track of conditions/columns involved and
 * their dependencies. And finally build the required query.
 * 
 * @author akiran
 */
public class ConditionQueryBuilder implements Cloneable
{
	public static final String DEF_TABLE_CODE = "T0";
	public static final String DEF_TABLE_ID_COL = "T0_ID";

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

	/**
	 * Condition details to be used in the query
	 * 
	 * @author akiran
	 */
	static class Condition
	{
		/**
		 * Condition operator
		 */
		private Operator operator;

		/**
		 * Index of the parameter defining this condition
		 */
		private int index;

		/**
		 * Embedded property name to be used when condition object in use
		 */
		private String embeddedProperty;

		/**
		 * Entity field expression
		 */
		@SuppressWarnings("unused")
		private String fieldExpression;

		/**
		 * Table on which this condition needs to be applied
		 */
		private TableInfo table;

		/**
		 * Field details (with column name) that can be used for condition
		 */
		private FieldDetails fieldDetails;

		/**
		 * Condition join operator
		 */
		private JoinOperator joinOperator;
		
		/**
		 * Indicates the condition should be case insensitive
		 */
		private boolean ignoreCase;

		/**
		 * Conditions grouped with current condition
		 */
		private List<Condition> groupedConditions;
		
		private boolean nullable;
		
		private Object defaultValue;
		
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
		public void addCondition(Condition condition)
		{
			if(groupedConditions == null)
			{
				groupedConditions = new ArrayList<Condition>();
			}

			groupedConditions.add(condition);
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

		public ParameterContext(Object[] parameters)
		{
			this.parameters = parameters;
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
	private List<Condition> conditions = new ArrayList<>();

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
	private int nextTableId = 1;

	/**
	 * Counter for generating unique field codes
	 */
	private int nextFieldId = 1;

	/**
	 * Indicates whether the expected result is single field and is direct
	 * return type (not a bean and sub property)
	 */
	private boolean isSingleFieldReturn;

	public ConditionQueryBuilder(EntityDetails entityDetails)
	{
		this.entityDetails = entityDetails;
		codeToTable.put(DEF_TABLE_CODE, new TableInfo(DEF_TABLE_CODE, entityDetails.getTableName(), entityDetails, null, null, null, false));
	}

	/**
	 * Generates new unique table code
	 * 
	 * @return
	 */
	private String nextTableCode()
	{
		return "T" + (nextTableId++);
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
		TableInfo newTableInfo = new TableInfo(nextTableCode(), tableName, entityDetails, joinTableCode, joinTableColumn, targetColumn, nullable);

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
		TableInfo currentTableInfo = codeToTable.get(DEF_TABLE_CODE), newTableInfo = null, joinTableInfo = null;
		FieldDetails fieldDetails = null, targetFieldDetails = null;
		int maxIndex = entityFieldPath.length - 1;
		ForeignConstraintDetails foreignConstraint = null, targetConstraint = null;
		JoinTableDetails joinTableDetails = null;

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
						throw new InvalidMappingException(String.format("Invalid field mapping '%1s' found in %2s parameter '%3s' of %4s. "
							+ "Extension field is used in middle/end of expression.", currentProp, paramType, conditionExpr, methodDesc));
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
					throw new InvalidMappingException(String.format("Invalid field mapping '%1s' found in %2s parameter '%3s' of %4s", currentProp, paramType, conditionExpr, methodDesc));
				}
			}

			// if end of field expression is reached
			if(i == maxIndex)
			{
				// if end field is found to be entity instead of simple property
				if(fieldDetails.isRelationField())
				{
					throw new InvalidMappingException(String.format("Non-simple field mapping '%1s' found as %2s parameter '%3s' of %4s", currentProp, paramType, conditionExpr, methodDesc));
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
				throw new InvalidMappingException(String.format("Non-relational field mapping '%1s' found in %2s parameter '%3s' of %4s", currentProp, paramType, conditionExpr, methodDesc));
			}

			foreignConstraint = fieldDetails.getForeignConstraintDetails();
			targetEntityDetails = foreignConstraint.getTargetEntityDetails();

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
							currentEntityDetails.getIdField().getDbColumnName(), targetFieldDetails.getDbColumnName(), currentProp, fieldDetails.isNullable());
				}
				// if table was joined via join talbe
				else
				{
					// add join table info
					joinTableInfo = newTableInfo(null, joinTableDetails.getTableName(), currentTableInfo.tableCode, currentEntityDetails.getIdField().getDbColumnName(), 
							joinTableDetails.getInverseJoinColumn(), null, fieldDetails.isNullable());

					// add target table info
					newTableInfo = newTableInfo(targetEntityDetails, targetEntityDetails.getTableName(), joinTableInfo.tableCode, 
							joinTableDetails.getJoinColumn(), targetEntityDetails.getIdField().getDbColumnName(), currentProp, fieldDetails.isNullable());
				}
			}
			// if the relation is via join table
			else if(foreignConstraint.getJoinTableDetails() != null)
			{
				joinTableDetails = foreignConstraint.getJoinTableDetails();

				// add join table info
				joinTableInfo = newTableInfo(null, joinTableDetails.getTableName(), currentTableInfo.tableCode, currentEntityDetails.getIdField().getDbColumnName(), 
						joinTableDetails.getJoinColumn(), null, fieldDetails.isNullable());

				// add target table info
				newTableInfo = newTableInfo(targetEntityDetails, targetEntityDetails.getTableName(), joinTableInfo.tableCode, 
						joinTableDetails.getInverseJoinColumn(), targetEntityDetails.getIdField().getDbColumnName(), currentProp, fieldDetails.isNullable());
			}
			// if the relation is simple relation
			else
			{
				newTableInfo = newTableInfo(targetEntityDetails, targetEntityDetails.getTableName(), currentTableInfo.tableCode, 
						fieldDetails.getDbColumnName(), targetEntityDetails.getIdField().getDbColumnName(), currentProp, fieldDetails.isNullable());
			}

			currentTableInfo = newTableInfo;
			currentEntityDetails = targetEntityDetails;
		}

		// only to satisfy compiler
		return null;
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
	public Condition addCondition(Condition groupHead, Operator operator, int index, String embeddedProperty, 
			String entityFieldExpression, JoinOperator joinOperator, String methodDesc, boolean nullable, boolean ignoreCase, String defaultValue)
	{
		// split the entity field expression
		String entityFieldParts[] = entityFieldExpression.trim().split("\\s*\\.\\s*");

		Condition condition = new Condition(operator, index, embeddedProperty, entityFieldExpression, joinOperator, nullable, ignoreCase);
		
		// if this mapping is for direct property mapping
		if(entityFieldParts.length == 1)
		{
			condition.fieldDetails = entityDetails.getFieldDetailsByField(entityFieldParts[0]);

			// if the field mapping is wrong
			if(condition.fieldDetails == null)
			{
				throw new InvalidMappingException(String.format("Invalid field mapping '%1s' found in condition parameter '%2s' of %3s", entityFieldExpression, condition.getConditionExpression(), methodDesc));
			}

			condition.table = codeToTable.get(DEF_TABLE_CODE);
			
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
					condition.defaultValue = ConvertUtils.convert(defaultValue, condition.fieldDetails.getField().getType());
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
				condition.defaultValue = ConvertUtils.convert(defaultValue, condition.fieldDetails.getField().getType());
			}catch(Exception ex)
			{
				throw new InvalidMappingException(String.format("Invalid default value specified for field %s for repository method %s", entityFieldExpression, methodDesc), ex);					
			}
		}

		return condition;
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
	public void addResultField(String resultProperty, Class<?> resultPropertyType, String entityFieldExpression, String methodDesc)
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

		ResultField resultField = new ResultField(resultProperty, nextFieldCode(), resultPropertyType);

		// if this mapping is for direct property mapping
		if(entityFieldParts.length == 1)
		{
			resultField.fieldDetails = entityDetails.getFieldDetailsByField(entityFieldParts[0]);

			// if the field mapping is wrong
			if(resultField.fieldDetails == null)
			{
				throw new InvalidMappingException(String.format("Invalid field mapping '%1s' found in result parameter '%2s' of '%3s'", entityFieldExpression, entityFieldExpression, methodDesc));
			}

			resultField.table = codeToTable.get(DEF_TABLE_CODE);
			resultFields.add(resultField);
			fieldToResultField.put(resultField.fieldDetails.getName(), resultField);

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

		// TODO: If end field represents a collection, check if it can be
		// supported, if not throw exception
		// Note - Reverse mapping property has to be created for such properties

		resultFields.add(resultField);
		fieldToResultField.put(resultField.fieldDetails.getName(), resultField);

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
	public void loadConditionalQuery(IConditionalQuery query, Object params[])
	{
		ParameterContext context = new ParameterContext(params);
		Set<String> includedTables = new HashSet<>();

		// load the result fields to specified query
		for(ResultField field : this.resultFields)
		{
			// add tables and fields to specifies query
			addTables(query, field.table.tableCode, includedTables);
			query.addResultField(new QueryResultField(field.table.tableCode, field.fieldDetails.getDbColumnName(), field.code));
		}

		query.addResultField(new QueryResultField(DEF_TABLE_CODE, codeToTable.get(DEF_TABLE_CODE).entityDetails.getIdField().getDbColumnName(), DEF_TABLE_ID_COL));

		QueryCondition queryCondition = null;
		
		// load the conditions to the query
		for(Condition condition : this.conditions)
		{
			queryCondition = buildConditionForQuery(context, includedTables, query, condition);
			
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
	private QueryCondition buildConditionForQuery(ParameterContext context, Set<String> includedTables, IConditionalQuery query, Condition condition)
	{
		Object value = null;
				
		// fetch the value for current condition
		try
		{
			//for non-method level conditions fetch the value
			if(condition.index >= 0)
			{
				value = PropertyUtils.getProperty(context, condition.getConditionExpression());
			}
			//for method level conditions like null-checks and others use default value from condition
			else
			{
				value = condition.defaultValue;
			}
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while fetching condition value for expression -" + condition.getConditionExpression(), ex);
		}
		
		QueryCondition groupHead = null;

		// if value is not provided, ignore current condition
		if(value != null || (condition.operator.isNullable() && condition.nullable))
		{
			// add tables and conditions to specifies query
			addTables(query, condition.table.tableCode, includedTables);
			
			groupHead = new QueryCondition(condition.table.tableCode, condition.fieldDetails.getDbColumnName(), condition.operator, value, condition.joinOperator, condition.ignoreCase);
		}

		//if no group conditions are present on this query
		if(condition.groupedConditions == null)
		{
			return groupHead;
		}
		
		//if group conditions are present add them recursively to current condition
		QueryCondition grpCondition = null;
		
		for(Condition grpInternalCondition : condition.groupedConditions)
		{
			grpCondition = buildConditionForQuery(context, includedTables, query, grpInternalCondition);
			
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

	public void loadOrderByFields(FinderQuery finderQuery)
	{
		if(this.orderByFields.isEmpty())
		{
			return;
		}

		List<QueryResultField> orderByCodes = new ArrayList<>();

		// loop through order by fields
		for(ResultField field : orderByFields)
		{
			orderByCodes.add(new QueryResultField(field.table.tableCode, field.fieldDetails.getDbColumnName(), field.code, field.orderType));
		}

		// if enable codes are available
		if(!orderByCodes.isEmpty())
		{
			// add order codes
			finderQuery.setOrderByFields(orderByCodes.toArray(new QueryResultField[0]));
		}

	}

	/**
	 * Creates a collection of specified type
	 * 
	 * @param type
	 * @return
	 */
	/*
	 * @SuppressWarnings("unchecked") private Collection<Object>
	 * createCollectionOfType(Class<?> type) { //if array list can be assigned
	 * to target field if(type.isAssignableFrom(ArrayList.class)) { return new
	 * ArrayList<>(); }
	 * 
	 * //if hashset can be assigned to target field
	 * if(type.isAssignableFrom(HashSet.class)) { return new HashSet<>(); }
	 * 
	 * try { return (Collection<Object>) type.newInstance(); }catch(Exception
	 * ex) { throw new IllegalStateException(
	 * "Unable to create collection of type - " + type.getName(), ex); } }
	 */

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
	private <T> T parseResult(Record record, Class<T> resultType, ConversionService conversionService, PersistenceExecutionContext persistenceExecutionContext) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException
	{
		if(isSingleFieldReturn)
		{
			ResultField resField = this.resultFields.get(0);
			Object res = conversionService.convertToJavaType(record.getObject(resField.code), resField.fieldDetails);

			return (T) ConvertUtils.convert(res, resField.fieldType);
		}

		T result = resultType.newInstance();
		Object value = null;
		ProxyEntityCreator proxyEntityCreator = null;
		ForeignConstraintDetails foreignConstraint = null;
		EntityDetails foreignEntityDetails = null;

		RepositoryFactory repositoryFactory = persistenceExecutionContext.getRepositoryFactory();

		for(ResultField resultField : this.resultFields)
		{
			value = record.getObject(resultField.code);

			// ignore null values
			if(value == null)
			{
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

					proxyEntityCreator = new ProxyEntityCreator(foreignEntityDetails, repositoryFactory.getRepositoryForEntity((Class) foreignEntityDetails.getEntityType()), value);
					value = proxyEntityCreator.getProxyEntity();
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
				throw new InvalidStateException(ex, "An error occurred while converting field {} value - {}", resultField.fieldDetails.getName(), value);
			}

			// if value is null after conversion, ignore value
			if(value == null)
			{
				continue;
			}

			PropertyUtils.setProperty(result, resultField.property, value);
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

	/**
	 * Fetches short code for the specified result field.
	 * 
	 * @param field
	 *            Field for which code needs to be fetched
	 * @return Matching code, if not present null will be returned.
	 */
	public QueryResultField getResultFieldCode(String field)
	{
		ResultField resultField = fieldToResultField.get(field);

		if(resultField == null)
		{
			return null;
		}

		return new QueryResultField(resultField.table.tableCode, resultField.fieldDetails.getDbColumnName(), resultField.code);
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
