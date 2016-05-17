package com.yukthi.indexer.search;

/**
 * Condition operators.
 * @author akiran
 */
public enum ConditionOperator
{
	/**
	 * Equals condition operator.
	 */
	EQ,
	
	/**
	 * Greater than or equals
	 */
	GTE,
	
	/**
	 * Less than or equals.
	 */
	LTE,
	
	/**
	 * Greater than.
	 */
	GT,
	
	/**
	 * Less than.
	 */
	LT,
	
	/**
	 * To be used only on analyzed fields
	 */
	AND;
}
