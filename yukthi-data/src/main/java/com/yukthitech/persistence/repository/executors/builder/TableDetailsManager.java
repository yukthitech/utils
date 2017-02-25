/*
 * 
 */
package com.yukthitech.persistence.repository.executors.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.yukthitech.persistence.RepositoryConfigurationException;
import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.ExtendedTableDetails;
import com.yukthitech.persistence.ExtendedTableEntity;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.ForeignConstraintDetails;
import com.yukthitech.persistence.JoinTableDetails;
import com.yukthitech.persistence.RelationType;

/**
 * Manages the tables information required by condition query builder.
 */
public class TableDetailsManager
{
	/**
	 * Mapping from property name to table
	 */
	private Map<String, TableInfo> propToTable = new HashMap<>();

	/**
	 * Mapping from table-short-code to the table. Which will be needed while
	 * giving join info
	 */
	private Map<String, TableInfo> codeToTable = new HashMap<>();
	
	/**
	 * Contains sub queries which are added for conditions.
	 */
	private Map<String, SubqueryBuilder> propToConditionQuery = new HashMap<>();

	TableInfo mainTableInfo;
	
	/**
	 * Used to generate unique table codes.
	 */
	private AtomicInteger nextTableId;

	/**
	 * Root entity details.
	 */
	private EntityDetails rootEntityDetails;
	
	/**
	 * Result column code for id field of default table
	 * (which gets added by default to result query)
	 */
	private String rootIdColumnCode;
	
	/**
	 * Instantiates a new table details manager.
	 *
	 * @param conditionQueryBuilder the condition query builder
	 */
	public TableDetailsManager(EntityDetails rootEntityDetails, AtomicInteger nextTableId, String rootIdColumnCode)
	{
		this.rootEntityDetails = rootEntityDetails;
		this.nextTableId = nextTableId;
	
		String mainTableCode = nextTableCode(rootEntityDetails);
		this.mainTableInfo = new TableInfo(null, null, rootEntityDetails, mainTableCode, null, false);
		
		codeToTable.put(mainTableCode, mainTableInfo);
	}
	
	public String getMainTableCode()
	{
		return mainTableInfo.getTableCode();
	}
	
	/**
	 * Generates new unique table code
	 * 
	 * @return
	 */
	private String nextTableCode(EntityDetails entityDetails)
	{
		//use entity class name as default table code
		String prefix = entityDetails.getSimpleName();
		prefix = prefix.replaceAll("([A-Z])", "_$1");
		prefix = prefix.startsWith("_") ? prefix.substring(1, prefix.length()) : prefix;
		
		return prefix + nextTableId.getAndIncrement();
	}
	
	/**
	 * Generates new unique code for subquery for specified field.
	 * @param entityDetails Entity details
	 * @param field Field for which query is being generated
	 * @return Unique code
	 */
	private String nextQueryCode(EntityDetails entityDetails, FieldDetails field)
	{
		String prefix = entityDetails.getSimpleName() + "_" + field.getName();
		prefix = prefix.replaceAll("([A-Z])", "_$1");
		prefix = prefix.startsWith("_") ? prefix.substring(1, prefix.length()) : prefix;

		return prefix + nextTableId.getAndIncrement();
	}
	
	/**
	 * Creates new table info with specified details and adds it to
	 * {@link #codeToTable} and {@link #propToTable} maps.
	 *
	 * @param sourceTable Source table to which new table has to be joined
	 * @param sourceTableColumn Source table column to which this new table should be joined
	 * @param entityDetails new table entity details
	 * @param targetColumn new table column
	 * @param nullable the nullable
	 * @param property the property
	 * @return newly created table info
	 */
	TableInfo newTableInfo(TableInfo sourceTable, String sourceTableColumn, EntityDetails entityDetails, String targetColumn, boolean nullable, String property)
	{
		TableInfo newTableInfo = new TableInfo(sourceTable, sourceTableColumn, entityDetails, nextTableCode(entityDetails), targetColumn, nullable);

		// add new table info to maps

		// property will be null for join tables
		if(property != null)
		{
			propToTable.put(property, newTableInfo);
		}

		codeToTable.put(newTableInfo.getTableCode(), newTableInfo);

		return newTableInfo;
	}

	/**
	 * Checks if the current property specified represents a extended field property. If yes, then extended
	 * table gets added and the same is returned.
	 * @param conditionQueryBuilder Condition query builder
	 * @param fieldParseInfo
	 * @param index Index at which extension needs to be checked
	 * @param currentProp Current property
	 * @param currentTableInfo table info to attach
	 * @param currentEntityDetails Current entity details
	 * @return Extended table info, if expressions represent extended property
	 */
	private TableInfo checkForExtendedTable(FieldParseInfo fieldParseInfo, int index, String currentProp, TableInfo currentTableInfo, EntityDetails currentEntityDetails)
	{
		String entityFieldPath[] = fieldParseInfo.entityFieldPath;
		ExtendedTableDetails extendedTableDetails = currentEntityDetails.getExtendedTableDetails();
		
		if(extendedTableDetails == null || !extendedTableDetails.getEntityField().getName().equals(entityFieldPath[index]))
		{
			return null;
		}

		//if current field represent extended field
		int maxIndex = entityFieldPath.length - 1;
		
		//if extended field is used in middle
		if(index != (maxIndex - 1))
		{
			throw new RepositoryConfigurationException("tableInfo.invalidExtensionPosition", fieldParseInfo.expression, fieldParseInfo.sourceField, fieldParseInfo.methodDesc);
		}
		
		EntityDetails extendedEntityDetails = extendedTableDetails.toEntityDetails(currentEntityDetails);
		
		//ensure valid extension field is specified
		if(extendedEntityDetails.getFieldDetailsByField(entityFieldPath[index + 1]) == null)
		{
			throw new RepositoryConfigurationException("tableInfo.invalidExtensionFieldName", 
				entityFieldPath[index + 1], fieldParseInfo.expression, fieldParseInfo.sourceField, fieldParseInfo.methodDesc);
		}
		
		TableInfo newTableInfo = propToTable.get(currentProp); 
		
		if(newTableInfo == null)
		{
			newTableInfo = newTableInfo(currentTableInfo, currentEntityDetails.getIdField().getDbColumnName(), 
					extendedEntityDetails, ExtendedTableEntity.COLUMN_ENTITY_ID, true, currentProp); 
		}

		fieldParseInfo.fieldDetails = extendedEntityDetails.getFieldDetailsByField(entityFieldPath[index + 1]);
		return newTableInfo;
	}

	private String getReminderPath(String entityFieldPath[], int fromIdx)
	{
		StringBuilder remainingEntityPath = new StringBuilder();
		
		for(int j = fromIdx + 1; j < entityFieldPath.length; j++)
		{
			remainingEntityPath.append(entityFieldPath[j]);
			
			if(j < entityFieldPath.length - 1)
			{
				remainingEntityPath.append(".");
			}
		}
		
		return remainingEntityPath.toString();
	}
	
	/**
	 * Adds the table info.
	 *
	 * @param conditionQueryBuilder the condition query builder
	 * @param fieldParseInfo the field parse info
	 * @return the table info
	 */
	public ITableDataSource addRequiredTables(FieldParseInfo fieldParseInfo)
	{
		String entityFieldPath[] = fieldParseInfo.entityFieldPath;
		EntityDetails targetEntityDetails = null, currentEntityDetails = rootEntityDetails;
		FieldDetails fieldDetails = null, targetFieldDetails = null;
		TableInfo currentTableInfo = this.mainTableInfo, newTableInfo = null, joinTableInfo = null;

		// if this mapping is for direct property mapping
		if(entityFieldPath.length == 1)
		{
			fieldDetails = currentEntityDetails.getFieldDetailsByField(entityFieldPath[0]);

			// if the field mapping is wrong
			if(fieldDetails == null)
			{
				throw new RepositoryConfigurationException("condition.invalidField", fieldParseInfo.expression, fieldParseInfo.sourceField, fieldParseInfo.methodDesc);
			}
			
			fieldParseInfo.tableInfo = mainTableInfo;
			fieldParseInfo.fieldDetails = fieldDetails;

			return mainTableInfo;
		}

		String currentProp = null;
		
		int maxIndex = entityFieldPath.length - 1;
		ForeignConstraintDetails foreignConstraint = null, targetConstraint = null;
		JoinTableDetails joinTableDetails = null;

		// loop through field parts and find the required table joins
			// excluding last part
		for(int i = 0; i < maxIndex; i++)
		{
			currentProp = (currentProp != null) ? currentProp + "." + entityFieldPath[i] : entityFieldPath[i];
			fieldDetails = currentEntityDetails.getFieldDetailsByField(entityFieldPath[i]);

			newTableInfo = propToTable.get(currentProp); 

			// if table is already found for current property
			if(newTableInfo != null)
			{
				currentTableInfo = newTableInfo;
				currentEntityDetails = (EntityDetails) newTableInfo.getEntityDetails();
				continue;
			}

			// if invalid field details encountered
			if(fieldDetails == null)
			{
				if( (newTableInfo = checkForExtendedTable(fieldParseInfo, i, currentProp, currentTableInfo, currentEntityDetails)) == null)
				{
					throw new RepositoryConfigurationException("tableInfo.invalidFieldMapping", 
							fieldParseInfo.expression, fieldParseInfo.sourceField, fieldParseInfo.methodDesc);
				}

				currentTableInfo = newTableInfo;
				currentEntityDetails = newTableInfo.getEntityDetails();
				continue;
			}

			if(!fieldDetails.isRelationField())
			{
				throw new RepositoryConfigurationException("tableInfo.nonRelationInMiddle", 
					currentProp, fieldParseInfo.expression, fieldParseInfo.sourceField, fieldParseInfo.methodDesc);
			}

			foreignConstraint = fieldDetails.getForeignConstraintDetails();
			targetEntityDetails = foreignConstraint.getTargetEntityDetails();
			
			//if relation is table owned
			if(!foreignConstraint.isMappedRelation())
			{
				//Note: One to many relation will not exist in un-mapped relation
				
				joinTableDetails = foreignConstraint.getJoinTableDetails();
				
				if(foreignConstraint.getRelationType() == RelationType.ONE_TO_ONE || foreignConstraint.getRelationType() == RelationType.MANY_TO_ONE)
				{
					//if join table is not involved
					if(joinTableDetails == null)
					{
						newTableInfo = newTableInfo(currentTableInfo, fieldDetails.getDbColumnName(), 
								targetEntityDetails, targetEntityDetails.getIdField().getDbColumnName(), fieldDetails.isNullable(), currentProp);
					}
					//if join table is involved
					else
					{
						joinTableDetails = foreignConstraint.getJoinTableDetails();

						// add join table info
						joinTableInfo = newTableInfo(currentTableInfo, currentEntityDetails.getIdField().getDbColumnName(), 
								joinTableDetails.toEntityDetails(), joinTableDetails.getJoinColumn(), fieldDetails.isNullable(), currentProp); 
								
						// add target table info
						newTableInfo = newTableInfo(joinTableInfo, joinTableDetails.getInverseJoinColumn(), 
								targetEntityDetails, targetEntityDetails.getIdField().getDbColumnName(), fieldDetails.isNullable(), currentProp); 
					}
				}
				//when case is MANY_TO_MANY
				else
				{
					SubqueryBuilder subqueryBuilder = propToConditionQuery.get(currentProp);
					String reminderPath = getReminderPath(entityFieldPath, i);
					
					if(subqueryBuilder == null)
					{
						subqueryBuilder = new SubqueryBuilder(nextQueryCode(currentEntityDetails, fieldDetails), currentTableInfo, foreignConstraint, nextTableId, false);
						propToConditionQuery.put(currentProp, subqueryBuilder);
					}
					
					FieldParseInfo subfieldParseInfo = new FieldParseInfo(fieldParseInfo.sourceField, reminderPath, fieldParseInfo.methodDesc);
					subqueryBuilder.tableDetailsManager.addRequiredTables(subfieldParseInfo); 
					
					return subqueryBuilder;
				}
			}
			//if relation is mapped one
			else
			{
				targetFieldDetails = targetEntityDetails.getFieldDetailsByField(foreignConstraint.getMappedBy());
				targetConstraint = targetFieldDetails.getForeignConstraintDetails();
				joinTableDetails = targetConstraint.getJoinTableDetails();

				//NOTE: Following relationships will not exist in mapped relations - MANY_TO_ONE
				
				if(foreignConstraint.getRelationType() == RelationType.ONE_TO_ONE)
				{
					//if join table is not involved
					if(joinTableDetails == null)
					{
						newTableInfo = newTableInfo(currentTableInfo, currentEntityDetails.getIdField().getName(), 
								targetEntityDetails, targetFieldDetails.getDbColumnName(), fieldDetails.isNullable(), currentProp);
					}
					//if join table is involved
					else
					{
						joinTableDetails = foreignConstraint.getJoinTableDetails();

						// add join table info
						joinTableInfo = newTableInfo(currentTableInfo, currentEntityDetails.getIdField().getDbColumnName(), 
								joinTableDetails.toEntityDetails(), joinTableDetails.getInverseJoinColumn(), fieldDetails.isNullable(), currentProp); 
								
						// add target table info
						newTableInfo = newTableInfo(joinTableInfo, joinTableDetails.getJoinColumn(), 
								targetEntityDetails, targetEntityDetails.getIdField().getDbColumnName(), fieldDetails.isNullable(), currentProp); 
					}
				}
				else
				{
					SubqueryBuilder subqueryBuilder = propToConditionQuery.get(currentProp);
					String reminderPath = getReminderPath(entityFieldPath, i);
					
					if(subqueryBuilder == null)
					{
						subqueryBuilder = new SubqueryBuilder(nextQueryCode(currentEntityDetails, fieldDetails), currentTableInfo, targetConstraint, nextTableId, true);
						propToConditionQuery.put(currentProp, subqueryBuilder);
					}
					
					subqueryBuilder.tableDetailsManager.addRequiredTables(new FieldParseInfo(fieldParseInfo.sourceField, reminderPath, fieldParseInfo.methodDesc));
					
					return subqueryBuilder;
				}
			}

			currentTableInfo = newTableInfo;
			rootEntityDetails = targetEntityDetails;
		}

		// if end field is found to be entity instead of simple property
		if(fieldDetails.isRelationField())
		{
			throw new RepositoryConfigurationException("tableInfo.invalidExpressionEnd", 
				fieldParseInfo.expression, fieldParseInfo.sourceField, fieldParseInfo.methodDesc);
		}

		fieldParseInfo.fieldDetails = fieldDetails;
		fieldParseInfo.tableInfo = currentTableInfo;

		return currentTableInfo;
	}
	
	/**
	 * Gets the result column code for id field of default table (which gets added by default to result query).
	 *
	 * @return the result column code for id field of default table (which gets added by default to result query)
	 */
	public String getRootIdColumnCode()
	{
		return rootIdColumnCode;
	}
}
