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
