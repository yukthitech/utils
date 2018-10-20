package com.yukthitech.utils.fmarker.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks target method as free marker method.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FreeMarkerMethod
{
	/**
	 * Name using which target method can be accessed from free marker templates.
	 * Defaults to target method name.
	 * @return
	 */
	public String value() default "";
	
	/**
	 * Description about the method.
	 * @return description.
	 */
	public String description() default "";
	
	/**
	 * Return value description.
	 * @return Return value description.
	 */
	public String returnDescription() default "";
	
	/**
	 * Used to document example for the method.
	 * @return example doc
	 */
	public ExampleDoc[] examples() default {};
}
