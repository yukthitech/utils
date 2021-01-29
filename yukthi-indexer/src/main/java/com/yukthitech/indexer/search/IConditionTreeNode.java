package com.yukthitech.indexer.search;

/**
 * Represents a condition tree node.
 * @author akiran
 */
public interface IConditionTreeNode
{
	public void setNextCondition(NextCondition nextCondition);
	
	public NextCondition getNextCondition();
}
