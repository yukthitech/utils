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
public @interface SearchGroupCondition
{
	/**
	 * Parent field name to use for all sub conditions. By default
	 * no parent field name will be attached.
	 * @return
	 */
	public String parentField() default "";
	
	/**
	 * Join operator under which this condition should be grouped.
	 * @return
	 */
	public JoinOperator joinWith() default JoinOperator.DEFAULT;

	/**
	 * Default join operator to be used between conditions of this subgroup.
	 * @return
	 */
	public JoinOperator defaultJoinOp() default JoinOperator.AND;
}
