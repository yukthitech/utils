package com.yukthi.persistence.query;

import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.query.data.TableStructure;

public class CreateTableQuery extends Query
{
	private TableStructure tableStructure;

	/**
	 * Indicates if this is join table or not
	 */
	private boolean isUniqueKeyDisabled;

	public CreateTableQuery(EntityDetails entityDetails)
	{
		super(entityDetails);

		this.tableStructure = new TableStructure(entityDetails);
	}

	public CreateTableQuery(EntityDetails entityDetails, boolean isUniqueKeyDisable)
	{
		this(entityDetails);
		this.isUniqueKeyDisabled = isUniqueKeyDisable;
	}

	public TableStructure getTableStructure()
	{
		return tableStructure;
	}

	public String getTableName()
	{
		return tableStructure.getTableName();
	}

	/**
	 * @return the {@link #isUniqueKeyDisabled isJoinTable}
	 */
	public boolean isUniqueKeyDisabled()
	{
		return isUniqueKeyDisabled;
	}

	/**
	 * @param isUniqueKeyDisabled
	 *            the {@link #isUniqueKeyDisabled isJoinTable} to set
	 */
	public void setUniqueKeyDisabled(boolean isUniqueKeyDisabled)
	{
		this.isUniqueKeyDisabled = isUniqueKeyDisabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(getClass().getName());
		return builder.toString();
	}
}
