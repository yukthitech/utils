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
	
	/**
	 * Should be set as true, if the value of the target param represents name of attribute being set.
	 * @return true if value represents name of attribute going to be set.
	 */
	public boolean attrName() default false;
	
	/**
	 * Default value used by this param.
	 * @return
	 */
	public String defaultValue() default "";
	
	/**
	 * Type of value expected for this field, post parsing.
	 * @return
	 */
	public Class<?> expectedType() default Object.class;
}
