package com.yukthitech.persistence.query;

import java.util.List;

import com.yukthitech.persistence.EntityDetails;

/**
 * Sub query that gets embedded in the main query.
 * @author akiran
 */
public class Subquery extends AbstractConditionalQuery
{
	/**
	 * Table code to be used for main table.
	 */
	private String mainTableCode;
	
	/**
	 * Instantiates a new subquery.
	 *
	 * @param entityDetails the entity details
	 * @param tableCode the table code
	 */
	public Subquery(EntityDetails entityDetails, String tableCode)
	{
		super(entityDetails);
		this.mainTableCode = tableCode;
	}

	/**
	 * Gets the table code to be used for main table.
	 *
	 * @return the table code to be used for main table
	 */
	public String getMainTableCode()
	{
		return mainTableCode;
	}

	/**
	 * Populates the parameters required conditions of this sub query.
	 * @param params List to which parameters needs to be populated.
	 */
	public void fetchQueryParameters(List<Object> params)
	{
		for(QueryCondition condition : super.getConditions())
		{
			condition.fetchQueryParameters(params);
		}
	}
}
