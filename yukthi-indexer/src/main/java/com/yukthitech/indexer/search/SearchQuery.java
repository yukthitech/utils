package com.yukthitech.indexer.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as search query and provides the details.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SearchQuery
{
	/**
	 * Default join operator to be used between conditions.
	 * @return
	 */
	public JoinOperator defaultJoinOp() default JoinOperator.DEFAULT;
}
