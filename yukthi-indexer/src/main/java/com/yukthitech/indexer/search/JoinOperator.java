package com.yukthitech.indexer.search;

/**
 * Join operator for search conditions.
 * @author akiran
 */
public enum JoinOperator
{
	/**
	 * Conditions specified with this operator must match. Equivalent of ADD.
	 */
	MUST,
	
	/**
	 * At least one of the conditions under this should match. Equivalent to OR.
	 */
	SHOULD,
	
	/**
	 * All of these clauses must not match. The equivalent of NOT.
	 */
	MUST_NOT,
	
	/**
	 * Clauses that must match, but are run in non-scoring, filtering mode.
	 */
	FILTER;
}
