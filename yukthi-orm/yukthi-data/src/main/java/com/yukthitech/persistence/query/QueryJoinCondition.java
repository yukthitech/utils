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
 * Represents a join condition in condition based query
 * 
 * @author akiran
 */
public class QueryJoinCondition
{
	/**
	 * joining table code
	 */
	private String joiningTableCode;

	/**
	 * Left side table column to be used for join condition
	 */
	private String joiningColumn;

	/**
	 * Right side table code for join condition
	 */
	private String parentTableCode;

	/**
	 * Right side table column for join condition
	 */
	private String parentColumn;

	/**
	 * New table name being joined
	 */
	private String joiningTableName;

	/**
	 * If the relation is nullable. If nullable, left join will be used
	 */
	private boolean nullable;

	/**
	 * Instantiates a new query join condition.
	 *
	 * @param leftTableCode
	 *            the left table code
	 * @param leftColumn
	 *            the left column
	 * @param rightTableCode
	 *            the right table code
	 * @param rightColumn
	 *            the right column
	 * @param nullable
	 *            the nullable
	 */
	public QueryJoinCondition(String leftTableCode, String leftColumn, String rightTableCode, String rightColumn, String rightJoinTableName, boolean nullable)
	{
		this.joiningTableCode = leftTableCode;
		this.joiningColumn = leftColumn;
		this.parentTableCode = rightTableCode;
		this.parentColumn = rightColumn;
		this.joiningTableName = rightJoinTableName;
		this.nullable = nullable;
	}

	/**
	 * Gets the joining table code.
	 *
	 * @return the joining table code
	 */
	public String getJoiningTableCode()
	{
		return joiningTableCode;
	}

	/**
	 * Sets the joining table code.
	 *
	 * @param joiningTableCode the new joining table code
	 */
	public void setJoiningTableCode(String joiningTableCode)
	{
		this.joiningTableCode = joiningTableCode;
	}

	/**
	 * Gets the left side table column to be used for join condition.
	 *
	 * @return the left side table column to be used for join condition
	 */
	public String getJoiningColumn()
	{
		return joiningColumn;
	}

	/**
	 * Sets the left side table column to be used for join condition.
	 *
	 * @param joiningColumn the new left side table column to be used for join condition
	 */
	public void setJoiningColumn(String joiningColumn)
	{
		this.joiningColumn = joiningColumn;
	}

	/**
	 * Gets the right side table code for join condition.
	 *
	 * @return the right side table code for join condition
	 */
	public String getParentTableCode()
	{
		return parentTableCode;
	}

	/**
	 * Sets the right side table code for join condition.
	 *
	 * @param parentTableCode the new right side table code for join condition
	 */
	public void setParentTableCode(String parentTableCode)
	{
		this.parentTableCode = parentTableCode;
	}

	/**
	 * Gets the right side table column for join condition.
	 *
	 * @return the right side table column for join condition
	 */
	public String getParentColumn()
	{
		return parentColumn;
	}

	/**
	 * Sets the right side table column for join condition.
	 *
	 * @param parentColumn the new right side table column for join condition
	 */
	public void setParentColumn(String parentColumn)
	{
		this.parentColumn = parentColumn;
	}

	/**
	 * Gets the new table name being joined.
	 *
	 * @return the new table name being joined
	 */
	public String getJoiningTableName()
	{
		return joiningTableName;
	}

	/**
	 * Sets the new table name being joined.
	 *
	 * @param joiningTableName the new new table name being joined
	 */
	public void setJoiningTableName(String joiningTableName)
	{
		this.joiningTableName = joiningTableName;
	}

	/**
	 * Checks if is if the relation is nullable. If nullable, left join will be used.
	 *
	 * @return the if the relation is nullable
	 */
	public boolean isNullable()
	{
		return nullable;
	}

	/**
	 * Sets the if the relation is nullable. If nullable, left join will be used.
	 *
	 * @param nullable the new if the relation is nullable
	 */
	public void setNullable(boolean nullable)
	{
		this.nullable = nullable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("[");

		builder.append(joiningTableCode).append(".").append(joiningColumn);
		builder.append(" = ").append(parentTableCode).append(".").append(parentColumn);

		if(nullable)
		{
			builder.append(" (+)");
		}

		return builder.toString();
	}
}
