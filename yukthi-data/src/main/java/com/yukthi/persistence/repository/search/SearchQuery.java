package com.yukthi.persistence.repository.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Search query object for search methods with dynamic conditions
 * 
 * @author akiran
 */
public class SearchQuery
{
	/**
	 * Dynamic conditions to be applied
	 */
	private List<SearchCondition> conditions = new ArrayList<>();
	
	/**
	 * List of fields by which search results should be ordered
	 */
	private List<String> orderByFields = new ArrayList<>();
	
	/**
	 * Count to which results should be limited. Used in results paging
	 */
	private int resultsLimitCount = -1;
	
	/**
	 * Instantiates a new search query.
	 */
	public SearchQuery()
	{}
	
	/**
	 * Instantiates a new search query.
	 *
	 * @param conditions the conditions
	 */
	public SearchQuery(SearchCondition... conditions)
	{
		for(SearchCondition cond : conditions)
		{
			this.addCondition(cond);
		}
	}

	/**
	 * Adds value to {@link #conditions conditions}
	 *
	 * @param condition
	 *            condition to be added
	 *            
	 * @return returns curent query to add more conditions
	 */
	public SearchQuery addCondition(SearchCondition condition)
	{
		conditions.add(condition);
		return this;
	}
	
	/**
	 * Sets the dynamic conditions to be applied.
	 *
	 * @param conditions the new dynamic conditions to be applied
	 */
	public void setConditions(List<SearchCondition> conditions)
	{
		this.conditions = conditions;
	}

	/**
	 * Gets the dynamic conditions to be applied.
	 *
	 * @return the dynamic conditions to be applied
	 */
	public List<SearchCondition> getConditions()
	{
		return conditions;
	}
	
	/**
	 * Gets the list of fields by which search results should be ordered.
	 *
	 * @return the list of fields by which search results should be ordered
	 */
	public List<String> getOrderByFields()
	{
		return orderByFields;
	}

	/**
	 * Sets the list of fields by which search results should be ordered.
	 *
	 * @param orderByFields the new list of fields by which search results should be ordered
	 */
	public void setOrderByFields(List<String> orderByFields)
	{
		this.orderByFields = orderByFields;
	}

	/**
	 * Gets the count to which results should be limited. Used in results paging.
	 *
	 * @return the count to which results should be limited
	 */
	public int getResultsLimitCount()
	{
		return resultsLimitCount;
	}

	/**
	 * Sets the count to which results should be limited. Used in results paging.
	 *
	 * @param resultsLimitCount the new count to which results should be limited
	 */
	public void setResultsLimitCount(int resultsLimitCount)
	{
		this.resultsLimitCount = resultsLimitCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Conditions: ").append(conditions);
		builder.append(", ").append("Order-by Fields: ").append(orderByFields);
		builder.append(", ").append("Results limit count: ").append(resultsLimitCount);

		builder.append("]");
		return builder.toString();
	}

}
