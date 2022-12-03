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

/**
 * Result field of the query
 * @author akiran
 */
public class QueryTable
{
	private String table;
	
	/**
	 * Table (code) to be used for this condition
	 */
	private String tableCode;

	public QueryTable(String table, String tableCode)
	{
		this.table = table;
		this.tableCode = tableCode;
	}

	/**
	 * @return the {@link #table table}
	 */
	public String getTable()
	{
		return table;
	}

	/**
	 * @param table the {@link #table table} to set
	 */
	public void setTable(String table)
	{
		this.table = table;
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("[");

		builder.append(table).append(" ").append(tableCode);
		builder.append("]");

		return builder.toString();
	}

}
