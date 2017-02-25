package com.yukthitech.indexer.search;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Encapsulation of required information for null or not null check.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NullCheck
{
	/**
	 * Field which should be checked for null or not null condition.
	 * @return
	 */
	public String field();
	
	/**
	 * Operator to be used to add this condition.
	 * @return
	 */
	public JoinOperator joinOperator() default JoinOperator.MUST;
	
	/**
	 * Boost factory for this condition.
	 * @return
	 */
	public int boost() default 0;
}
