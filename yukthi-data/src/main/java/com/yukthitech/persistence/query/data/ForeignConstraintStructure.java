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
package com.yukthitech.persistence.query.data;

import java.lang.reflect.Field;
import java.util.Map;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.ForeignConstraintDetails;

public class ForeignConstraintStructure
{
	private String name;
	private String columns[];
	private String parentTable;
	private String parentColumns[];
	private boolean isDeleteCascaded;

	public ForeignConstraintStructure(EntityDetails entityDetails, ForeignConstraintDetails constraint, Map<String, String> fieldMapping, boolean isDeleteCascaded)
	{
		Field ownerField = constraint.getOwnerField();
		EntityDetails targetEntity = constraint.getTargetEntityDetails();

		this.name = constraint.getConstraintName();
		this.parentTable = targetEntity.getTableName();

		this.columns = new String[] { entityDetails.getFieldDetailsByField(ownerField.getName()).getDbColumnName() };
		this.parentColumns = new String[] { targetEntity.getIdField().getDbColumnName() };
		this.isDeleteCascaded = isDeleteCascaded;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String[] getColumns()
	{
		return columns;
	}

	public void setColumns(String[] columns)
	{
		this.columns = columns;
	}

	public String getParentTable()
	{
		return parentTable;
	}

	public void setParentTable(String parentTable)
	{
		this.parentTable = parentTable;
	}

	public String[] getParentColumns()
	{
		return parentColumns;
	}

	public void setParentColumns(String[] parentColumns)
	{
		this.parentColumns = parentColumns;
	}

	/**
	 * @return the {@link #isDeleteCascaded isDeleteCascaded}
	 */
	public boolean isDeleteCascaded()
	{
		return isDeleteCascaded;
	}

	/**
	 * @param isDeleteCascaded
	 *            the {@link #isDeleteCascaded isDeleteCascaded} to set
	 */
	public void setDeleteCascaded(boolean isDeleteCascaded)
	{
		this.isDeleteCascaded = isDeleteCascaded;
	}

}
