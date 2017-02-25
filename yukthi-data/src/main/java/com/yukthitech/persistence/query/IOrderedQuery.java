package com.yukthitech.persistence.query;

import java.util.List;

/**
 * Represents query which supports ordered execution or results.
 * @author akiran
 */
public interface IOrderedQuery
{
	/**
	 * Adds specified order by field to the query.
	 * @param orderByField Field to add
	 */
	public void addOrderByField(QueryResultField orderByField);
	
	/**
	 * Gets the fields in which results/execution should be ordered. 
	 *
	 * @return the fields in which results/execution should be ordered
	 */
	public List<QueryResultField> getOrderByFields();
}
