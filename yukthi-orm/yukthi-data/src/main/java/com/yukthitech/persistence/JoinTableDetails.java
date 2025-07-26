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
package com.yukthitech.persistence;

import java.lang.reflect.Field;

import com.yukthitech.persistence.annotations.DataType;

/**
 * Represents join table (intermediate table) that is needed for many-to-many
 * relation
 * 
 * @author akiran
 */
public class JoinTableDetails
{
	/**
	 * Join table name
	 */
	private String tableName;

	/**
	 * Join column name. Used to link with the owner entity table
	 */
	private String joinColumn;
	
	/**
	 * Data type of join column
	 */
	private DataType joinColumnType;

	/**
	 * Inverse join column name. Used to link with non-owner entity table
	 * (target table)
	 */
	private String inverseJoinColumn;
	
	/**
	 * Data type of inverse join column
	 */
	private DataType inverseJoinColumnType;
	
	/**
	 * Represents this join table as entity details
	 */
	private EntityDetails entityDetailsForm;
	
	/**
	 * Entity details owning this join table
	 */
	private EntityDetails ownerEntityDetails;
	
	/**
	 * Target Entity details by target 
	 */
	private EntityDetails targetEntityDetails;
	
	public JoinTableDetails()
	{}
	
	public JoinTableDetails(String tableName, String joinColumn, DataType joinColumnType, String inverseJoinColumn, DataType inverseJoinColumnType,
			EntityDetails ownerEntityDetails, EntityDetails targetEntityDetails)
	{
		this.tableName = tableName;
		this.joinColumn = joinColumn;
		this.inverseJoinColumn = inverseJoinColumn;
		
		this.joinColumnType = joinColumnType;
		this.inverseJoinColumnType = inverseJoinColumnType;
		
		this.ownerEntityDetails = ownerEntityDetails;
		this.targetEntityDetails = targetEntityDetails;
	}

	/**
	 * @return the {@link #tableName tableName}
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * @param tableName
	 *            the {@link #tableName tableName} to set
	 */
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	/**
	 * @return the {@link #joinColumn joinColumn}
	 */
	public String getJoinColumn()
	{
		return joinColumn;
	}

	/**
	 * @param joinColumn
	 *            the {@link #joinColumn joinColumn} to set
	 */
	public void setJoinColumn(String joinColumn)
	{
		this.joinColumn = joinColumn;
	}

	/**
	 * @return the {@link #inverseJoinColumn inverseJoinColumn}
	 */
	public String getInverseJoinColumn()
	{
		return inverseJoinColumn;
	}

	/**
	 * @param inverseJoinColumn
	 *            the {@link #inverseJoinColumn inverseJoinColumn} to set
	 */
	public void setInverseJoinColumn(String inverseJoinColumn)
	{
		this.inverseJoinColumn = inverseJoinColumn;
	}
	
	/**
	 * @return the {@link #joinColumnType joinColumnType}
	 */
	public DataType getJoinColumnType()
	{
		return joinColumnType;
	}

	/**
	 * @param joinColumnType the {@link #joinColumnType joinColumnType} to set
	 */
	public void setJoinColumnType(DataType joinColumnType)
	{
		this.joinColumnType = joinColumnType;
	}

	/**
	 * @return the {@link #inverseJoinColumnType inverseJoinColumnType}
	 */
	public DataType getInverseJoinColumnType()
	{
		return inverseJoinColumnType;
	}

	/**
	 * @param inverseJoinColumnType the {@link #inverseJoinColumnType inverseJoinColumnType} to set
	 */
	public void setInverseJoinColumnType(DataType inverseJoinColumnType)
	{
		this.inverseJoinColumnType = inverseJoinColumnType;
	}
	
	public EntityDetails getOwnerEntityDetails()
	{
		return ownerEntityDetails;
	}

	/**
	 * Converts this join table details to entity details
	 * @return
	 */
	public EntityDetails toEntityDetails()
	{
		//if entity details was already created, reuse it
		if(entityDetailsForm != null)
		{
			return entityDetailsForm;
		}
		
		try
		{
			Class<?> entityType = JoinTableEntity.class;
			
			Field joinColumnField = entityType.getDeclaredField("joinColumn");
			Field invJoinColField = entityType.getDeclaredField("inverseJoinColumn");
			
			//create entity details
			EntityDetails entityDetails = new EntityDetails(tableName, entityType);
			
			FieldDetails joinFieldDetails = new FieldDetails(joinColumnField, joinColumnType, false, false);
			joinFieldDetails.setDbColumnName(joinColumn);
			
			FieldDetails invJoinFieldDetails = new FieldDetails(invJoinColField, inverseJoinColumnType, false, false);
			invJoinFieldDetails.setDbColumnName(inverseJoinColumn);
			
			entityDetails.addFieldDetails(joinFieldDetails);
			entityDetails.addFieldDetails(invJoinFieldDetails);
			
			//TODO: Check how to fetch join-table uq constraint name for already existing tables
			entityDetails.addUniqueKeyConstraint(new UniqueConstraintDetails(entityDetails, "UQ_" + entityDetails.getTableName(), 
					new String[]{"joinColumn", "inverseJoinColumn"}, "Specified relation already exist", true, true));
			
			String FK_PREFIX = ForeignConstraintDetails.FOREIGN_CONSTRAINT_PREFIX + entityDetails.getTableName() + "_";
		
			entityDetails.addForeignConstraintDetails(new ForeignConstraintDetails(FK_PREFIX + "SRC", ownerEntityDetails, joinColumnField, entityDetails));
			entityDetails.addForeignConstraintDetails(new ForeignConstraintDetails(FK_PREFIX + "TGT", targetEntityDetails, invJoinColField, entityDetails));
			
			entityDetails.addIndexDetails(new IndexDetails("JOIN_COL", new String[]{"joinColumn"}));
			entityDetails.addIndexDetails(new IndexDetails("INV_JOIN_COL", new String[]{"inverseJoinColumn"}));
			
			this.entityDetailsForm = entityDetails;
			
			return entityDetails;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while converting JoinTableDetails to EntityDetails", ex);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Table: ").append(tableName);
		builder.append(",").append("Join Column: ").append(joinColumn);
		builder.append(",").append("Inverse Join Column: ").append(inverseJoinColumn);

		builder.append("]");
		return builder.toString();
	}

}
