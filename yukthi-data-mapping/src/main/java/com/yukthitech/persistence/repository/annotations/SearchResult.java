package com.yukthitech.persistence.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark return type of finder method to have embedded result fields
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface SearchResult
{
	/**
	 * Specifies mapping from entity fields to result properties. If specified, @{@link Field} annotations
	 * will not be searched in the return type of finder method.
	 * @return Mapping from entity field to result property
	 */
	public ResultMapping[] mappings() default {};
}
