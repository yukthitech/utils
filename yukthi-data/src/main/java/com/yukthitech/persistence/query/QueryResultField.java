package com.yukthitech.persistence.query;

import com.yukthitech.persistence.repository.annotations.OrderByType;

/**
 * Result field of the query
 * @author akiran
 */
public class QueryResultField
{
	/**
	 * Table (code) to be used for this condition
	 */
	private String tableCode;
	
	/**
	 * Column to be used for this condition
	 */
	private String column;
	
	/**
	 * Short code for the column field
	 */
	private String code;
	
	private OrderByType orderByType;

	public QueryResultField(String tableCode, String column, String code)
	{
		this.tableCode = tableCode;
		this.column = column;
		this.code = code;
	}

	public QueryResultField(String tableCode, String column, String code, OrderByType orderByType)
	{
		this.tableCode = tableCode;
		this.column = column;
		this.code = code;
		this.orderByType = orderByType;
	}
	/**
	 * @return the {@link #tableCode tableCode}
	 */
	public String getTableCode()
	{
		return tableCode;
	}

	/**
	 * @param tableCode the {@link #tableCode tableCode} to set
	 */
	public void setTableCode(String tableCode)
	{
		this.tableCode = tableCode;
	}

	/**
	 * @return the {@link #column column}
	 */
	public String getColumn()
	{
		return column;
	}

	/**
	 * @param column the {@link #column column} to set
	 */
	public void setColumn(String column)
	{
		this.column = column;
	}

	/**
	 * @return the {@link #code code}
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code the {@link #code code} to set
	 */
	public void setCode(String code)
	{
		this.code = code;
	}
	
	/**
	 * @return the {@link #orderByType orderByType}
	 */
	public OrderByType getOrderByType()
	{
		return orderByType;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("[");

		builder.append(tableCode).append(".").append(column);
		builder.append("]");

		return builder.toString();
	}
}
