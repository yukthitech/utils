package com.yukthitech.persistence.repository.executors.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.ForeignConstraintDetails;
import com.yukthitech.persistence.InvalidMappingException;
import com.yukthitech.persistence.Record;
import com.yukthitech.persistence.RepositoryConfigurationException;
import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.query.IConditionalQuery;
import com.yukthitech.persistence.query.IOrderedQuery;
import com.yukthitech.persistence.query.QueryCondition;
import com.yukthitech.persistence.query.QueryResultField;
import com.yukthitech.persistence.repository.PersistenceExecutionContext;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.persistence.repository.annotations.OrderByType;
import com.yukthitech.persistence.repository.executors.proxy.ProxyEntityCreator;
import com.yukthitech.persistence.repository.search.DynamicResultField;
import com.yukthitech.persistence.repository.search.IDynamicSearchResult;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Builder that can be used to keep track of conditions/columns involved and
 * their dependencies. And finally build the required query.
 * 
 * @author akiran
 */
public class ConditionQueryBuilder implements Cloneable
{
	/**
	 * Represents inner query condition.
	 * @author akiran
	 *
	 */
	/*
	static class InnerQuery
	{
		/**
		 * Current field table to which sub query result should be compared.
		 * /
		FieldDetails currentTableField;
		
		/**
		 * Sub query field which needs to be fetched from subquery.
		 * /
		FieldDetails subqueryField;
		
		/**
		 * Condition query builder for building sub query.
		 * /
		ConditionQueryBuilder subqueryBuilder;
		
		JoinOperator joinOperator;
		
		public InnerQuery(FieldDetails currentTableField, FieldDetails subqueryField, EntityDetails subqueryEntity, JoinOperator joinOperator, ConditionQueryBuilder conditionQueryBuilder)
		{
			this.currentTableField = currentTableField;
			this.subqueryField = subqueryField;
			
			this.subqueryBuilder = new ConditionQueryBuilder(subqueryEntity, conditionQueryBuilder.nextTableCode(subqueryEntity), conditionQueryBuilder.nextFieldCode(), conditionQueryBuilder.nextTableId);
			this.joinOperator = joinOperator;
		}
	}
	
	static class IntermediateQuery
	{
		private String resultProperty;
		
		private String mappingColumnCode;
		
		private IntermediateQueryExecutor intermediateQueryExecutor;

		public IntermediateQuery(String resultProperty, String mappingColumnCode, IntermediateQueryExecutor intermediateQueryExecutor)
		{
			this.resultProperty = resultProperty;
			this.mappingColumnCode = mappingColumnCode;
			this.intermediateQueryExecutor = intermediateQueryExecutor;
		}
	}
	*/

	/**
	 * Entity details on which this query is going to be executed
	 */
	EntityDetails entityDetails;
	
	/**
	 * Description of the method for which condition query builder is being built.
	 */
	private String methodDesc;

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
	 * Mapping from result field to intermediate query required to populate field.
	 */
	//private Map<String, IntermediateQuery> fieldToIntermediateQuery = new HashMap<>();

	private List<ResultField> orderByFields = new ArrayList<>();

	/**
	 * Counter for generating unique field codes
	 */
	private int nextFieldId = 1;

	/**
	 * Indicates whether the expected result is single field and is direct
	 * return type (not a bean and sub property)
	 */
	private boolean isSingleFieldReturn;

	/**
	 * Table details manager which takes care of required table addition.
	 */
	protected TableDetailsManager tableDetailsManager;

	public ConditionQueryBuilder(EntityDetails entityDetails, String methodDesc)
	{
		this(entityDetails, new AtomicInteger(1));
		this.methodDesc = methodDesc;
	}

	protected ConditionQueryBuilder(EntityDetails entityDetails, AtomicInteger nextTableId)
	{
		this.entityDetails = entityDetails;
		tableDetailsManager = new TableDetailsManager(entityDetails, nextTableId, nextFieldCode());
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

	/*
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
	*/

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
	public Condition addCondition(Condition condition)
	{
		//Condition condition = new Condition(operator, index, embeddedProperty, entityFieldExpression, joinOperator, nullable, ignoreCase);
		condition.joiningField = isJoiningField(condition.fieldExpression);

		FieldParseInfo fieldParseInfo = new FieldParseInfo(condition.getConditionExpression(), condition.fieldExpression, methodDesc);
		ITableDataSource tableDataSource = tableDetailsManager.addRequiredTables(fieldParseInfo);

		this.conditions.add(condition);
		condition.table = fieldParseInfo.tableInfo;
		condition.fieldDetails = fieldParseInfo.fieldDetails;
		condition.tableDataSource = tableDataSource;
		
		//if the condition requires sub query, add the current condition to subquery condition list
			// such conditions should be ignored while building the queries
		if(tableDataSource instanceof ConditionQueryBuilder)
		{
			((ConditionQueryBuilder) tableDataSource).conditions.add(condition);
		}

		return condition;
	}
	
	/*
	public InnerQuery addFieldSubquery(Condition groupHead, Operator operator, int index, String embeddedProperty, 
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
	*/

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
	
	/*
	private boolean checkAndAddIntermediateQuery(String resultProperty, Class<?> resultPropertyType, Type resultGenericPropertyType, 
			String entityFieldParts[], String methodDesc)
	{
		EntityDetails currentEntityDetails = this.entityDetails;
		FieldDetails fieldDetails = null;
		
		boolean needsIntermediateQuery = false;
		StringBuilder currentPath = new StringBuilder();
		
		ForeignConstraintDetails foreignConstraintDetails = null;
		
		String fieldName = null, mainMappingField = null;
		Class<?> finalFieldType = null;
		String childProperty = null;
		EntityDetails intermediateEntityDetails = null;
		Class<?> mappingFieldType = null;
		
		for(int i = 0; i < entityFieldParts.length; i++)
		{
			fieldName = entityFieldParts[i];
			
			fieldDetails = currentEntityDetails.getFieldDetailsByField(fieldName);
			
			//if normal field is encountered in middle
			if(!fieldDetails.isRelationField())
			{
				//if intermediate query is not required
				if(!needsIntermediateQuery)
				{
					return false;
				}
				
				if(i != entityFieldParts.length - 1)
				{
					throw new RepositoryConfigurationException("intermediate.query.midNonRelationField", fieldName, resultProperty);
				}
				
				finalFieldType = fieldDetails.getField().getType();
			}

			foreignConstraintDetails = fieldDetails.getForeignConstraintDetails(); 
			
			//if MANY relation is found in path
			if(foreignConstraintDetails.getRelationType() == RelationType.ONE_TO_MANY || foreignConstraintDetails.getRelationType() == RelationType.MANY_TO_MANY)
			{
				if(!Collection.class.isAssignableFrom(resultPropertyType))
				{
					throw new RepositoryConfigurationException("intermediate.query.nonCollectionResultField", fieldName, resultProperty);
				}
				
				//fetch the mapping field path in main query
				currentPath.append(currentEntityDetails.getIdField().getField().getName());
				mainMappingField = currentPath.toString();
				mappingFieldType = currentEntityDetails.getIdField().getField().getType();
				currentPath.setLength(0);
				
				needsIntermediateQuery = true;
				intermediateEntityDetails = currentEntityDetails;
			}
			
			currentEntityDetails = foreignConstraintDetails.getTargetEntityDetails();
			currentPath.append(fieldName).append(".");
		}
		
		//remove trailing dot
		currentPath.deleteCharAt(currentPath.length() - 1);
		
		childProperty = currentPath.toString();

		//if intermediate query is not required simply return false.
		if(!needsIntermediateQuery)
		{
			return false;
		}
		
		if(finalFieldType == null)
		{
			throw new RepositoryConfigurationException("intermediate.query.noFinalField", resultProperty);
		}
		
		String mappingFieldCode = addResultField("$" + resultProperty, mappingFieldType, mappingFieldType, mainMappingField, methodDesc);
		
		String interMethodDesc = methodDesc + "[Result Field: " + resultProperty + "]";
		IntermediateQueryExecutor intermediateQueryExecutor = new IntermediateQueryExecutor(intermediateEntityDetails.getEntityType(), 
				intermediateEntityDetails, interMethodDesc);
		
		intermediateQueryExecutor.setMappingField(intermediateEntityDetails.getIdField().getName(), intermediateEntityDetails.getIdField().getField().getType());
		intermediateQueryExecutor.setResultField(childProperty, finalFieldType);
		
		fieldToIntermediateQuery.put(resultProperty, new IntermediateQuery(resultProperty, mappingFieldCode, intermediateQueryExecutor));
		return true;
	}
	*/
	
	/**
	 * Adds a result field of the query to this builder
	 * 
	 * @param resultProperty
	 * @param entityFieldExpression
	 * @param methodDesc
	 * @return Field code representing the newly added field.
	 */
	public String addResultField(ResultField resultField)
	{
		// if a field is already added as direct return value
		if(isSingleFieldReturn)
		{
			throw new RepositoryConfigurationException("result.secondReturn", methodDesc);
		}

		/*
		 * resultProperty would be null only when direct field return is expected.
		 * In case if result fields are already added and later direct result is being added as expectation throw error.
		 */
		if(!this.resultFields.isEmpty() && resultField.property == null)
		{
			throw new RepositoryConfigurationException("result.additionalDirectReturn", methodDesc);
		}

		// split the entity field expression
		//String entityFieldParts[] = entityFieldExpression.trim().split("\\s*\\.\\s*");
		
		/*
		if(checkAndAddIntermediateQuery(resultProperty, resultPropertyType, resultGenericPropertyType, entityFieldParts, methodDesc))
		{
			return null;
		}
		*/

		//ResultField resultField = new ResultField(resultProperty, nextFieldCode(), resultPropertyType);
		FieldParseInfo fieldParseInfo = new FieldParseInfo(resultField.property, resultField.entityFieldExpression, methodDesc);
		ITableDataSource tableDataSource = tableDetailsManager.addRequiredTables(fieldParseInfo);
		
		resultField.fieldDetails = fieldParseInfo.fieldDetails;
		resultField.table = fieldParseInfo.tableInfo;
		resultField.code = nextFieldCode();
		resultField.tableDataSource = tableDataSource;
		
		if(resultField.property == null)
		{
			isSingleFieldReturn = true;
		}
		
		if(tableDataSource instanceof ConditionQueryBuilder)
		{
			((ConditionQueryBuilder)tableDataSource).resultFields.add(resultField);
		}
		
		resultFields.add(resultField);
		fieldToResultField.put(resultField.fieldDetails.getName(), resultField);

		// TODO: If end field represents a collection, check if it can be
		// supported, if not throw exception
		// Note - Reverse mapping property has to be created for such properties

		return resultField.code;
	}

	/**
	 * Adds specified table and its dependency tables to the specified
	 * conditional query.
	 * 
	 * @param query
	 * @param tableCode
	 * @param includedTables
	 */
	/*
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
				reverseOrder.add(0, new QueryJoinCondition(tableInfo.sourceTableCode, tableInfo.column, tableInfo.joinTableCode, tableInfo.joinTableColumn, tableInfo.sourceTableName, tableInfo.nullable));
			}

			currentCode = tableInfo.joinTableCode;
		}
		
		for(QueryJoinCondition condition : reverseOrder)
		{
			query.addJoinCondition(condition);
		}
	}
	*/

	/**
	 * Loads the conditions, tables and fields to the specified conditional
	 * query using specified params
	 * 
	 * @param query
	 * @param params
	 */
	public void loadConditionalQuery(Object repoExecutionContext, IConditionalQuery query, Object params[])
	{
		query.setMainTableCode(tableDetailsManager.getMainTableCode());
		
		QueryBuilderContext context = new QueryBuilderContext(this, params, repoExecutionContext, tableDetailsManager.getMainTableCode());
		loadConditionalQuery(context, query);
	}
	
	private void loadConditionalQuery(QueryBuilderContext context, IConditionalQuery query)
	{
		// load the result fields to specified query
		for(ResultField field : this.resultFields)
		{
			// add tables and fields to specifies query
			field.addRequiredTables(context, query);
			
			query.addResultField(new QueryResultField(field.table.tableCode, field.fieldDetails.getDbColumnName(), field.code));
		}

		query.addResultField(
			new QueryResultField(tableDetailsManager.getMainTableCode(), tableDetailsManager.mainTableInfo.entityDetails.getIdField().getDbColumnName(), tableDetailsManager.getRootIdColumnCode())
		);

		QueryCondition queryCondition = null;
		
		// load the conditions to the query
		for(Condition condition : this.conditions)
		{
			queryCondition = buildQueryCondition(context, query, condition);
			
			if(queryCondition != null)
			{
				query.addCondition(queryCondition);
			}
		}
	}
	
	/**
	 * Builds conditions recursively that needs to be added to query
	 * @param context Parameter context
	 * @param query Query for which conditions are being built
	 * @param condition Condition to be converted
	 * @return Newly built query condition. If value is null, returns null.
	 */
	private QueryCondition buildQueryCondition(QueryBuilderContext context, 
			IConditionalQuery query, Condition condition)
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
				String strValue = condition.defaultValue;
				
				//if no repo context is specified assume empty object
				Object repoContext = (context.repositoryExecutionContext != null) ? context.repositoryExecutionContext : Collections.emptyMap(); 
				
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
		
		QueryCondition queryCondition = null;

		// if value is not provided, ignore current condition
		if(value != null || (condition.operator.isNullable() && condition.nullable))
		{
			// add tables and conditions to specifies query
			condition.addRequiredTables(context, query);
			
			queryCondition = new QueryCondition(condition.table.tableCode, condition.fieldDetails.getDbColumnName(), condition.operator, value, condition.joinOperator, condition.ignoreCase);
		}

		return queryCondition;
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
			newBuilder.conditions = new ArrayList<>(conditions);
			newBuilder.resultFields = new ArrayList<>(resultFields);
			newBuilder.orderByFields = new ArrayList<>(orderByFields);
			newBuilder.fieldToResultField = new HashMap<>(fieldToResultField);

			return newBuilder;
		} catch(CloneNotSupportedException ex)
		{
			throw new IllegalStateException("An error occurred while cloning", ex);
		}
	}
}
