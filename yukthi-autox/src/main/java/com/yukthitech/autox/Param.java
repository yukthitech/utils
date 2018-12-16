package com.yukthitech.autox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to define possible parameters of different plugables of this
 * framework. Which in turn is used for generating documentation.
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Param
{
	/**
	 * Name of the parameter, by default takes the field name.
	 * @return name of the param
	 */
	public String name() default "";
	
	/**
	 * Description about the parameter.
	 * @return Description about the parameter.
	 */
	public String description();
	
	/**
	 * Indicates whether this is mandatory parameter or not.
	 * @return mandatory or not
	 */
	public boolean required() default true;
	
	/**
	 * Indicates the parameter represents a resource string.
	 * @return flag indicating if resource or not. Default false.
	 */
	public SourceType sourceType() default SourceType.NONE;
}
