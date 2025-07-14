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
