package com.yukthi.persistence.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to set ordering for finder method results
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface OrderBy
{
	/**
	 * If specified, the output will be ordered according to specified fields
	 * @return Fields to use for results ordering
	 */
	public String[] value() default {};
	
	/**
	 * If specified, the output will be ordered according to specified fields. If this 
	 * attribute is specified, value() will not be ignored.
	 * @return  Fields to use for results ordering
	 */
	public OrderByField[] fields() default {};
}
