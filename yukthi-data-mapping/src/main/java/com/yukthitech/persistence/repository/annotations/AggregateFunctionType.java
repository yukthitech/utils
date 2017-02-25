package com.yukthitech.persistence.repository.annotations;

/**
 * Aggregation function name to use.
 * @author akiran
 */
public enum AggregateFunctionType
{
	/**
	 * Count function.
	 */
	COUNT, 
	
	/**
	 * Minimum function.
	 */
	MIN, 
	
	/**
	 * Maximum function.
	 */
	MAX, 
	
	/**
	 * Average function.
	 */
	AVG;
}
