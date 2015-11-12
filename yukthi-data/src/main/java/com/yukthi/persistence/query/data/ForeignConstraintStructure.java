package com.yukthi.persistence.query.data;

import java.lang.reflect.Field;
import java.util.Map;

import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.ForeignConstraintDetails;

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

		this.columns = new String[] { entityDetails.getFieldDetailsByField(ownerField.getName()).getColumn() };
		this.parentColumns = new String[] { targetEntity.getIdField().getColumn() };
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
