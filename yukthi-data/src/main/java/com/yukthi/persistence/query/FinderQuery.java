package com.yukthi.persistence.query;

import com.yukthi.persistence.EntityDetails;

/**
 * Finder query object used to search entities based on specified conditions
 * @author akiran
 */
public class FinderQuery extends AbstractConditionalQuery
{
	/**
	 * fields in which results should be ordered
	 */
	private QueryResultField orderByFields[];
	
	/**
	 * Count to which results should be limited
	 */
	private Integer resultsLimitCount;
	
	/**
	 * Instantiates a new finder query.
	 *
	 * @param entityDetails the entity details
	 */
	public FinderQuery(EntityDetails entityDetails)
	{
		super(entityDetails);
	}

	/**
	 * Gets the fields in which results should be ordered.
	 *
	 * @return the fields in which results should be ordered
	 */
	public QueryResultField[] getOrderByFields()
	{
		return orderByFields;
	}

	/**
	 * Sets the fields in which results should be ordered.
	 *
	 * @param orderByFields the new fields in which results should be ordered
	 */
	public void setOrderByFields(QueryResultField[] orderByFields)
	{
		this.orderByFields = orderByFields;
	}

	/**
	 * Gets the count to which results should be limited.
	 *
	 * @return the count to which results should be limited
	 */
	public Integer getResultsLimitCount()
	{
		return resultsLimitCount;
	}

	/**
	 * Sets the count to which results should be limited.
	 *
	 * @param resultsLimitCount the new count to which results should be limited
	 */
	public void setResultsLimitCount(Integer resultsLimitCount)
	{
		this.resultsLimitCount = resultsLimitCount;
	}
	
	
}
