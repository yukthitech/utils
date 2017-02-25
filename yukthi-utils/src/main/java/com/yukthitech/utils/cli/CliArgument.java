package com.yukthitech.utils.cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to map a field of a bean from command line argument. 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CliArgument
{
	/**
	 * Name of the argument.
	 * @return argument name
	 */
	public String name();
	
	/**
	 * Long name of the argument.
	 * @return long argument name.
	 */
	public String longName() default "";
	
	/**
	 * Description of the argument.
	 * @return description
	 */
	public String description();
	
	/**
	 * Specifies if this argument is mandatory. Defaults to true.
	 * @return true if mandatory.
	 */
	public boolean required() default true;
}
