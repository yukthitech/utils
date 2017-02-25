package com.yukthitech.utils.expr;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Helps in marking function as expression function and provides information about the function.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FunctionInfo
{
	/**
	 * Name of the function.
	 * @return Name of the function
	 */
	public String name() default "";
	
	/**
	 * If the return type of the function, depends on the parameters then 
	 * this parameter to be used to specify the parameter list on which function return
	 * type is based. In case of distinct parameters types, the common base class will be used
	 * as return type.
	 * @return Parameter indexes whose type should be considered.
	 */
	public int[] matchParameterTypes() default {};
	
	/**
	 * Simple description about the function.
	 * @return Description about the function.
	 */
	public String description();
	
	/**
	 * Syntax of the function.
	 * @return function syntax.
	 */
	public String syntax();
}
