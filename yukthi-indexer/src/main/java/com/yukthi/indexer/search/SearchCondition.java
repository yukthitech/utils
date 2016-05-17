package com.yukthi.indexer.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as part of search condition.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SearchCondition
{
	/**
	 * Field on which this condition should be applied. Defaults to current field name.
	 * @return
	 */
	public String field() default "";
	
	/**
	 * Join operator under which this condition should be grouped.
	 * @return
	 */
	public JoinOperator joinOperator() default JoinOperator.MUST;
	
	/**
	 * Condition operator to be used for thie condition.
	 * @return
	 */
	public ConditionOperator op() default ConditionOperator.EQ;
	
	/**
	 * Boost value to be used for boosting this condition.
	 * @return
	 */
	public int boost() default 0;
	
	/**
	 * Minimum match in percentage (eg: 60%) or word count (Eg: 5)
	 * @return
	 */
	public String minMatch() default "";
	
}
