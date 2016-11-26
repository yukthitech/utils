package com.yukthi.persistence.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark repository function as aggregate query function.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface AggregateFunction
{
	/**
	 * Specifies aggregation function to use. Defaults to COUNT.
	 * @return Function to use.
	 */
	public AggregateFunctionType type() default AggregateFunctionType.COUNT;
	
	/**
	 * Specifies field to use in aggregation function.
	 * @return Defaults to id field.
	 */
	public String field() default "";
}
