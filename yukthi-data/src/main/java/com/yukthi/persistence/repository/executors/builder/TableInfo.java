package com.yukthi.persistence.repository.executors.builder;

import com.yukthi.persistence.EntityDetails;

/**
 * Table information required by the query.
 */
class TableInfo
{
	/**
	 * Short code for table.
	 */
	private String tableCode;

	/**
	 * Table name.
	 */
	private String tableName;

	/**
	 * Entity details representing this table.
	 */
	private EntityDetails entityDetails;

	/**
	 * Table to which this table has to join (in order to get joined
	 * with main table).
	 */
	private TableInfo joinTable;

	/**
	 * Column to be used in current table for join.
	 */
	private String joinTableColumn;

	/**
	 * Column to be used in target table for join.
	 */
	private String column;
	
	/**
	 * Indicates if this relation is nullable.
	 */
	private boolean nullable;

	/**
	 * Instantiates a new table info.
	 *
	 * @param tableCode the table code
	 * @param tableName the table name
	 * @param entityDetails the entity details
	 * @param joinTable the join table code
	 * @param sourceColumn the source column
	 * @param targetColumn the target column
	 * @param nullable the nullable
	 */
	public TableInfo(String tableCode, String tableName, EntityDetails entityDetails, TableInfo joinTable, String sourceColumn, String targetColumn, boolean nullable)
	{
		this.tableCode = tableCode;
		this.tableName = tableName;
		this.entityDetails = entityDetails;
		this.joinTable = joinTable;
		this.joinTableColumn = sourceColumn;
		this.column = targetColumn;
		this.nullable = nullable;
	}

	/**
	 * Gets the short code for table.
	 *
	 * @return the short code for table
	 */
	public String getTableCode()
	{
		return tableCode;
	}

	/**
	 * Gets the table name.
	 *
	 * @return the table name
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * Gets the entity details representing this table.
	 *
	 * @return the entity details representing this table
	 */
	public EntityDetails getEntityDetails()
	{
		return entityDetails;
	}
	
	/**
	 * Gets the table to which this table has to join (in order to get joined with main table).
	 *
	 * @return the table to which this table has to join (in order to get joined with main table)
	 */
	public TableInfo getJoinTable()
	{
		return joinTable;
	}

	/**
	 * Gets the column to be used in current table for join.
	 *
	 * @return the column to be used in current table for join
	 */
	public String getJoinTableColumn()
	{
		return joinTableColumn;
	}

	/**
	 * Gets the column to be used in target table for join.
	 *
	 * @return the column to be used in target table for join
	 */
	public String getColumn()
	{
		return column;
	}

	/**
	 * Checks if is indicates if this relation is nullable.
	 *
	 * @return the indicates if this relation is nullable
	 */
	public boolean isNullable()
	{
		return nullable;
	}
}
