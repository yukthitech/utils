package com.yukthi.indexer.search;

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
	 * Type which is used for indexing. This helps in understanding if search needs to be done on exact value
	 * or for full text search.
	 * @return
	 */
	public Class<?> indexType();
	
	/**
	 * Fields whose absence needs to be added to the query.
	 * @return
	 */
	public NullCheck[] nullFields() default {};
	
	/**
	 * Fields whose presence needs to be added to the query.
	 * @return
	 */
	public NullCheck[] notNullFields() default {};
	
	/**
	 * If set to true, score independent (constant score) search query will be executed. Defaults to false.
	 * @return
	 */
	public boolean ignoreScore() default false;
}
