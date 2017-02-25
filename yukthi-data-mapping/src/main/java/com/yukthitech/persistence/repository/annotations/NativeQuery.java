package com.yukthitech.persistence.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark target function as native query executor. Native queries should be avoided to the extent possible so
 * that the application is target db independent.
 * 
 * Native queries are added by using 
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface NativeQuery
{
	/**
	 * Specifies of the name of the native query to execute
	 * @return
	 */
	public String name();
	
	/**
	 * Specifies the type of the native query. Default type: READ
	 * @return
	 */
	public NativeQueryType type() default NativeQueryType.READ;
}
