package com.yukthitech.indexer.search;

/**
 * Join operator for search conditions.
 * @author akiran
 */
public enum JoinOperator
{
	AND,
	
	OR,
	
	/**
	 * Indicates the default join operator specified on search-query or group-query to
	 * be used.
	 */
	DEFAULT
}
