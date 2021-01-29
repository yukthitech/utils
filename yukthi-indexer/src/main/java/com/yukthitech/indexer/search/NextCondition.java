package com.yukthitech.indexer.search;

/**
 * Linking to next condition along with join operator.
 * @author akiran
 */
public class NextCondition
{
	/**
	 * Join operator to next condition.
	 */
	private JoinOperator joinOperator;
	
	/**
	 * Next condition to be linked.
	 */
	private IConditionTreeNode condition;

	/**
	 * Instantiates a new next condition.
	 *
	 * @param joinOperator the join operator
	 * @param condition the condition
	 */
	public NextCondition(JoinOperator joinOperator, IConditionTreeNode condition)
	{
		this.joinOperator = joinOperator;
		this.condition = condition;
	}

	/**
	 * Gets the join operator to next condition.
	 *
	 * @return the join operator to next condition
	 */
	public JoinOperator getJoinOperator()
	{
		return joinOperator;
	}

	/**
	 * Gets the next condition to be linked.
	 *
	 * @return the next condition to be linked
	 */
	public IConditionTreeNode getCondition()
	{
		return condition;
	}
}
