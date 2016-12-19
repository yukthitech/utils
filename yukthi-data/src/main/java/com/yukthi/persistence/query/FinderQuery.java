package com.yukthi.persistence.query;

import java.util.ArrayList;
import java.util.List;

import com.yukthi.persistence.EntityDetails;

/**
 * Finder query object used to search entities based on specified conditions
 * @author akiran
 */
public class FinderQuery extends AbstractConditionalQuery implements IOrderedQuery
{
	/**
	 * fields in which results should be ordered
	 */
	private List<QueryResultField> orderByFields;
	
	/**
	 * Row number after which results should be fetched. Used in paging.
	 */
	private Integer resultsOffset;

	/**
	 * Count to which results should be limited
	 */
	private Integer resultsLimit;
	
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
	@Override
	public List<QueryResultField> getOrderByFields()
	{
		return orderByFields;
	}

	/**
	 * Sets the fields in which results should be ordered.
	 *
	 * @param orderByFields the new fields in which results should be ordered
	 */
	public void setOrderByFields(List<QueryResultField> orderByFields)
	{
		this.orderByFields = orderByFields;
	}

	/**
	 * Adds specified order by field to the query.
	 * @param orderByField Field to add
	 */
	@Override
	public void addOrderByField(QueryResultField orderByField)
	{
		if(this.orderByFields == null)
		{
			this.orderByFields = new ArrayList<>();
		}
		
		this.orderByFields.add(orderByField);
	}

	/**
	 * Gets the count to which results should be limited.
	 *
	 * @return the count to which results should be limited
	 */
	public Integer getResultsLimit()
	{
		return resultsLimit;
	}
	
	/**
	 * Fetches result limit as string.
	 * @return
	 */
	public String getResultsLimitString()
	{
		return "" + resultsLimit;
	}

	/**
	 * Sets the count to which results should be limited.
	 *
	 * @param resultsLimit the new count to which results should be limited
	 */
	public void setResultsLimit(Integer resultsLimit)
	{
		this.resultsLimit = resultsLimit;
	}

	/**
	 * Gets the row number after which results should be fetched. Used in paging.
	 *
	 * @return the row number after which results should be fetched
	 */
	public Integer getResultsOffset()
	{
		return resultsOffset;
	}

	/**
	 * Sets the row number after which results should be fetched. Used in paging.
	 *
	 * @param resultsOffset the new row number after which results should be fetched
	 */
	public void setResultsOffset(Integer resultsOffset)
	{
		this.resultsOffset = resultsOffset;
	}
	
	/**
	 * Gets the results offset as string.
	 * @return
	 */
	public String getResultsOffsetString()
	{
		return "" + resultsOffset;
	}
}
