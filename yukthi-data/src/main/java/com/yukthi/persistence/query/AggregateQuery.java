package com.yukthi.persistence.query;

import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.repository.annotations.AggregateFunctionType;

/**
 * Represents aggregate query.
 */
public class AggregateQuery extends AbstractConditionalQuery
{
	/**
	 * Aggregate function to use.
	 */
	private AggregateFunctionType aggregateFunction;
	
	/**
	 * Aggregate column to use.
	 */
	private String aggregateColumn;

	/**
	 * Instantiates a new aggregate query.
	 *
	 * @param entityDetails the entity details
	 * @param aggregateFunction the aggregate function
	 * @param aggregateColumn the aggrgate column
	 */
	public AggregateQuery(EntityDetails entityDetails, AggregateFunctionType aggregateFunction, String aggregateColumn)
	{
		super(entityDetails);
		this.aggregateFunction = aggregateFunction;
		this.aggregateColumn = aggregateColumn;
	}

	/**
	 * Gets the aggregate function to use.
	 *
	 * @return the aggregate function to use
	 */
	public AggregateFunctionType getAggregateFunction()
	{
		return aggregateFunction;
	}

	/**
	 * Gets the aggregate column to use.
	 *
	 * @return the aggregate column to use
	 */
	public String getAggregateColumn()
	{
		return aggregateColumn;
	}

	@Override
	protected void toStringPrefix(StringBuilder builder)
	{
		builder.append("\n\tAggr Function: ").append(aggregateFunction);
		builder.append("\n\tAggr Column: ").append(aggregateColumn);
	}
}
