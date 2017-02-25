package com.yukthitech.persistence.repository.executors.builder;

import java.util.concurrent.atomic.AtomicInteger;

import com.yukthitech.persistence.ForeignConstraintDetails;

public class SubqueryBuilder extends ConditionQueryBuilder implements ITableDataSource
{
	/**
	 * Name of this subquery. So that on need basis this
	 * can be used as temp table or with in conditions.
	 */
	private String code;
	
	public SubqueryBuilder(String name, TableInfo parentTable, ForeignConstraintDetails foreignConstraint, AtomicInteger nextTableId, boolean mappedRelation)
	{
		super(mappedRelation ? foreignConstraint.getTargetEntityDetails() : foreignConstraint.getOwnerEntityDetails(), nextTableId);
		
		TableInfo mainTableInfo = null;
		
		if(!mappedRelation)
		{
			TableInfo joinTableInfo = super.tableDetailsManager.newTableInfo(parentTable, foreignConstraint.getOwnerEntityDetails().getIdField().getDbColumnName(), 
					foreignConstraint.getJoinTableDetails().toEntityDetails(), foreignConstraint.getJoinTableDetails().getJoinColumn(), false, "#JOIN_1_" + name);

			mainTableInfo = super.tableDetailsManager.newTableInfo(joinTableInfo, foreignConstraint.getJoinTableDetails().getInverseJoinColumn(), 
					foreignConstraint.getTargetEntityDetails(), foreignConstraint.getTargetEntityDetails().getIdField().getDbColumnName(), false, "#JOIN_2_" + name);
		}
		else
		{
			TableInfo joinTableInfo = super.tableDetailsManager.newTableInfo(parentTable, foreignConstraint.getTargetEntityDetails().getIdField().getDbColumnName(), 
					foreignConstraint.getJoinTableDetails().toEntityDetails(), foreignConstraint.getJoinTableDetails().getInverseJoinColumn(), false, "#JOIN_1_" + name);

			mainTableInfo = super.tableDetailsManager.newTableInfo(joinTableInfo, foreignConstraint.getJoinTableDetails().getJoinColumn(), 
					foreignConstraint.getTargetEntityDetails(), foreignConstraint.getOwnerEntityDetails().getIdField().getDbColumnName(), false, "#JOIN_2_" + name);
		}
		
		super.tableDetailsManager.mainTableInfo = mainTableInfo; 
	}
	
	/**
	 * Gets the name of this subquery. So that on need basis this can be used as temp table or with in conditions.
	 *
	 * @return the name of this subquery
	 */
	public String getCode()
	{
		return code;
	}
}
