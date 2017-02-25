package com.yukthitech.persistence.repository.executors.builder;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.query.IConditionalQuery;
import com.yukthitech.persistence.query.QueryJoinCondition;

/**
 * Table information required by the query.
 */
class TableInfo implements ITableDataSource
{
	/**
	 * In the target query the source table from which current table should
	 * be joined.
	 */
	TableInfo sourceTable;
	
	/**
	 * Short code column to which current table should be joined with.
	 */
	String sourceTableColumn;

	/**
	 * Current table entity details.
	 */
	EntityDetails entityDetails;
	
	/**
	 * Current table code to be used.
	 */
	String tableCode;

	/**
	 * Current table Column using which join should happen.
	 */
	String column;
	
	/**
	 * Indicates if this relation is nullable. Based on this join appropriate join will be used.
	 */
	boolean nullable;

	/**
	 * Instantiates a new table info.
	 *
	 * @param sourceTable the source table
	 * @param sourceTableColumn the source table column
	 * @param entityDetails the entity details
	 * @param tableCode the table code
	 * @param column the column
	 * @param nullable the nullable
	 */
	public TableInfo(TableInfo sourceTable, String sourceTableColumn, EntityDetails entityDetails, String tableCode, String column, boolean nullable)
	{
		this.sourceTable = sourceTable;
		this.sourceTableColumn = sourceTableColumn;
		this.entityDetails = entityDetails;
		this.tableCode = tableCode;
		this.column = column;
		this.nullable = nullable;
	}

	/**
	 * Gets the in the target query the source table from which current table should be joined.
	 *
	 * @return the in the target query the source table from which current table should be joined
	 */
	public ITableDataSource getSourceTable()
	{
		return sourceTable;
	}

	/**
	 * Gets the short code column to which current table should be joined with.
	 *
	 * @return the short code column to which current table should be joined with
	 */
	public String getSourceTableColumn()
	{
		return sourceTableColumn;
	}

	/**
	 * Gets the current table entity details.
	 *
	 * @return the current table entity details
	 */
	public EntityDetails getEntityDetails()
	{
		return entityDetails;
	}

	/**
	 * Gets the current table code to be used.
	 *
	 * @return the current table code to be used
	 */
	public String getTableCode()
	{
		return tableCode;
	}

	/**
	 * Gets the current table Column using which join should happen.
	 *
	 * @return the current table Column using which join should happen
	 */
	public String getColumn()
	{
		return column;
	}

	/**
	 * Checks if is indicates if this relation is nullable. Based on this join appropriate join will be used.
	 *
	 * @return the indicates if this relation is nullable
	 */
	public boolean isNullable()
	{
		return nullable;
	}

	public void addRequiredTables(QueryBuilderContext context, IConditionalQuery query)
	{
		//if current table was already added, ignore this time
		if(context.isTableInUse(tableCode))
		{
			return;
		}
		
		//add the source table before adding current one
		if(sourceTable != null)
		{
			sourceTable.addRequiredTables(context, query);
		}
		
		//join current table to the query
		QueryJoinCondition joinCondition = new QueryJoinCondition(sourceTable.tableCode, sourceTableColumn, 
				tableCode, column, entityDetails.getTableName(), nullable);
		query.addJoinCondition(joinCondition);
	}
}
