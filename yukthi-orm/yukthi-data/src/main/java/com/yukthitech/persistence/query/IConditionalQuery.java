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
 * Abstraction of condition queries like - Select, Delete and Update
 * @author akiran
 */
public interface IConditionalQuery
{
	/**
	 * Adds a condition for this query
	 * @param condition
	 */
	public void addCondition(QueryCondition condition);

	/**
	 * Adds a join condition for this query
	 * @param condition
	 */
	public void addJoinCondition(QueryJoinCondition condition);

	/**
	 * Adds result field for this query
	 * @param resultField
	 */
	public void addResultField(QueryResultField resultField);
	
	/**
	 * Sets the default table code to be used.
	 *
	 * @param defaultTableCode the new default table code to be used
	 */
	public void setDefaultTableCode(String defaultTableCode);
	
	/**
	 * Adds table name to this query 
	 */
	//public void addTable(QueryTable table);
	
	
}
