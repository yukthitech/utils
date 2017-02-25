package com.yukthitech.indexer.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates target field should be used as subquery.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Subquery
{
	/**
	 * Join operator under which this condition should be grouped.
	 * @return
	 */
	public JoinOperator joinOperator() default JoinOperator.MUST;
}
