package com.yukthitech.indexer.search;

/**
 * The Class ConditionGroup.
 */
public class ConditionGroup implements IConditionTreeNode
{
	/**
	 * Root condition of this group.
	 */
	private IConditionTreeNode condition;
	
	/**
	 * Next condition in the link.
	 */
	private NextCondition nextCondition;

	/**
	 * Instantiates a new condition group.
	 *
	 * @param condition the condition
	 * @param nextCondition the next condition
	 */
	public ConditionGroup(IConditionTreeNode condition, NextCondition nextCondition)
	{
		this.condition = condition;
		this.nextCondition = nextCondition;
	}

	/**
	 * Gets the root condition of this group.
	 *
	 * @return the root condition of this group
	 */
	public IConditionTreeNode getCondition()
	{
		return condition;
	}

	/**
	 * Sets the root condition of this group.
	 *
	 * @param condition the new root condition of this group
	 */
	void setCondition(Condition condition)
	{
		this.condition = condition;
	}

	/**
	 * Sets the next condition in the link.
	 *
	 * @param nextCondition the new next condition in the link
	 */
	@Override
	public void setNextCondition(NextCondition nextCondition)
	{
		this.nextCondition = nextCondition;
	}
	
	/**
	 * Gets the next condition in the link.
	 *
	 * @return the next condition in the link
	 */
	@Override
	public NextCondition getNextCondition()
	{
		return nextCondition;
	}
}
