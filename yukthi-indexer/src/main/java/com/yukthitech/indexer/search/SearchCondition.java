package com.yukthitech.indexer.search;

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
	public JoinOperator joinWith() default JoinOperator.DEFAULT;
	
	public MatchOperator matchOp() default MatchOperator.NONE;
}
