package com.yukthi.persistence.repository.executors.builder;

import com.yukthi.persistence.ConfigurationErrorException;
import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.ExtendedTableDetails;
import com.yukthi.persistence.ExtendedTableEntity;
import com.yukthi.persistence.FieldDetails;
import com.yukthi.persistence.ForeignConstraintDetails;
import com.yukthi.persistence.JoinTableDetails;

/**
 * The Class TableDetailsManager.
 */
public class TableDetailsManager
{
	/**
	 * Checks if the current property specified represents a extended field property. If yes, then extended
	 * table gets added and the same is returned.
	 * @param conditionQueryBuilder Condition query builder
	 * @param fieldParseInfo
	 * @param index Index at which extension needs to be checked
	 * @param currentProp
	 * @param currentTableInfo
	 * @return Extended table info, if expressions represent extended property
	 */
	private TableInfo checkForExtendedTable(ConditionQueryBuilder conditionQueryBuilder, FieldParseInfo fieldParseInfo, 
			int index, String currentProp, TableInfo currentTableInfo)
	{
		EntityDetails currentEntityDetails = fieldParseInfo.entityDetails;
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
			throw new ConfigurationErrorException("tableInfo.invalidExtensionPosition", fieldParseInfo.expression, fieldParseInfo.sourceField, fieldParseInfo.methodDesc);
		}
		
		EntityDetails extendedEntityDetails = extendedTableDetails.toEntityDetails(currentEntityDetails);
		
		//ensure valid extension field is specified
		if(extendedEntityDetails.getFieldDetailsByField(entityFieldPath[index + 1]) == null)
		{
			throw new ConfigurationErrorException("tableInfo.invalidExtensionFieldName", 
				entityFieldPath[index + 1], fieldParseInfo.expression, fieldParseInfo.sourceField, fieldParseInfo.methodDesc);
		}
		
		TableInfo newTableInfo = conditionQueryBuilder.getTableInfoForProperty(currentProp); 
		
		if(newTableInfo == null)
		{
			newTableInfo = conditionQueryBuilder.newTableInfo(extendedEntityDetails, extendedEntityDetails.getTableName(), currentTableInfo, 
					currentEntityDetails.getIdField().getDbColumnName(), ExtendedTableEntity.COLUMN_ENTITY_ID, currentProp, true);
		}

		fieldParseInfo.fieldDetails = extendedEntityDetails.getFieldDetailsByField(entityFieldPath[index + 1]);
		return newTableInfo;
	}
	
	/**
	 * Adds the table info.
	 *
	 * @param conditionQueryBuilder the condition query builder
	 * @param fieldParseInfo the field parse info
	 * @return the table info
	 */
	public TableInfo addTableInfo(ConditionQueryBuilder conditionQueryBuilder, FieldParseInfo fieldParseInfo)
	{
		EntityDetails currentEntityDetails = fieldParseInfo.entityDetails, targetEntityDetails = null;
		String currentProp = null;
		TableInfo currentTableInfo = conditionQueryBuilder.getMainTableInfo(), newTableInfo = null, joinTableInfo = null;
		FieldDetails fieldDetails = null, targetFieldDetails = null;
		
		String entityFieldPath[] = fieldParseInfo.entityFieldPath;
		int maxIndex = entityFieldPath.length - 1;
		ForeignConstraintDetails foreignConstraint = null, targetConstraint = null;
		JoinTableDetails joinTableDetails = null;

		// loop through field parts and find the required table joins
		for(int i = 0; i < maxIndex; i++)
		{
			currentProp = (currentProp != null) ? currentProp + "." + entityFieldPath[i] : entityFieldPath[i];
			fieldDetails = currentEntityDetails.getFieldDetailsByField(entityFieldPath[i]);

			// if invalid field details encountered
			if(fieldDetails == null)
			{
				if( (newTableInfo = checkForExtendedTable(conditionQueryBuilder, fieldParseInfo, i, currentProp, currentTableInfo)) == null)
				{
					throw new ConfigurationErrorException("tableInfo.invalidFieldMapping", 
							fieldParseInfo.expression, fieldParseInfo.sourceField, fieldParseInfo.methodDesc);
				}
			}

			newTableInfo = conditionQueryBuilder.getTableInfoForProperty(currentProp); 

			// if table is already found for current property
			if(newTableInfo != null)
			{
				currentTableInfo = newTableInfo;
				currentEntityDetails = newTableInfo.getEntityDetails();
				continue;
			}

			if(!fieldDetails.isRelationField())
			{
				throw new ConfigurationErrorException("tableInfo.nonRelationInMiddle", 
					currentProp, fieldParseInfo.expression, fieldParseInfo.sourceField, fieldParseInfo.methodDesc);
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
					newTableInfo = conditionQueryBuilder.newTableInfo(targetEntityDetails, targetEntityDetails.getTableName(), currentTableInfo, 
							currentEntityDetails.getIdField().getDbColumnName(), targetFieldDetails.getDbColumnName(), currentProp, fieldDetails.isNullable());
				}
				// if table was joined via join talbe
				else
				{
					// add join table info
					joinTableInfo = conditionQueryBuilder.newTableInfo(null, joinTableDetails.getTableName(), currentTableInfo, currentEntityDetails.getIdField().getDbColumnName(), 
							joinTableDetails.getInverseJoinColumn(), null, fieldDetails.isNullable());

					// add target table info
					newTableInfo = conditionQueryBuilder.newTableInfo(targetEntityDetails, targetEntityDetails.getTableName(), joinTableInfo, 
							joinTableDetails.getJoinColumn(), targetEntityDetails.getIdField().getDbColumnName(), currentProp, fieldDetails.isNullable());
				}
			}
			// if the relation is via join table
			else if(foreignConstraint.getJoinTableDetails() != null)
			{
				joinTableDetails = foreignConstraint.getJoinTableDetails();

				// add join table info
				joinTableInfo = conditionQueryBuilder.newTableInfo(null, joinTableDetails.getTableName(), currentTableInfo, currentEntityDetails.getIdField().getDbColumnName(), 
						joinTableDetails.getJoinColumn(), null, fieldDetails.isNullable());

				// add target table info
				newTableInfo = conditionQueryBuilder.newTableInfo(targetEntityDetails, targetEntityDetails.getTableName(), joinTableInfo, 
						joinTableDetails.getInverseJoinColumn(), targetEntityDetails.getIdField().getDbColumnName(), currentProp, fieldDetails.isNullable());
			}
			// if the relation is simple relation
			else
			{
				newTableInfo = conditionQueryBuilder.newTableInfo(targetEntityDetails, targetEntityDetails.getTableName(), currentTableInfo, 
						fieldDetails.getDbColumnName(), targetEntityDetails.getIdField().getDbColumnName(), currentProp, fieldDetails.isNullable());
			}

			currentTableInfo = newTableInfo;
			currentEntityDetails = targetEntityDetails;
		}

		// if end field is found to be entity instead of simple property
		if(fieldDetails.isRelationField())
		{
			
			throw new ConfigurationErrorException("tableInfo.invalidExpressionEnd", 
				fieldParseInfo.expression, fieldParseInfo.sourceField, fieldParseInfo.methodDesc);
		}

		fieldParseInfo.fieldDetails = fieldDetails;
		return currentTableInfo;
	}
}
