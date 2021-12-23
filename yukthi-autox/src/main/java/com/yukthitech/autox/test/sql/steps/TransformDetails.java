package com.yukthitech.autox.test.sql.steps;

/**
 * Details available during transformation.
 * @author akranthikiran
 */
public class TransformDetails
{
	/**
	 * Name of column whose value is being transformed.
	 */
	private String columnName;
	
	/**
	 * Current column value being transformed.
	 */
	private Object columnValue;
	
	/**
	 * Current row.
	 */
	private Object row;

	/**
	 * Instantiates a new transform details.
	 *
	 * @param columnName
	 *            name of column whose value is being transformed.
	 * @param columnValue
	 *            current column value being transformed.
	 * @param row
	 *            current row.
	 */
	public TransformDetails(String columnName, Object columnValue, Object row)
	{
		this.columnName = columnName;
		this.columnValue = columnValue;
		this.row = row;
	}

	/**
	 * Gets the name of column whose value is being transformed.
	 *
	 * @return the name of column whose value is being transformed
	 */
	public String getColumnName()
	{
		return columnName;
	}

	/**
	 * Gets the current column value being transformed.
	 *
	 * @return the current column value being transformed
	 */
	public Object getColumnValue()
	{
		return columnValue;
	}

	/**
	 * Gets the current row.
	 *
	 * @return the current row
	 */
	public Object getRow()
	{
		return row;
	}
}
