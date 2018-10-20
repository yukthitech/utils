package com.yukthitech.utils.fmarker.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to give examples of the target.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ExampleDoc
{
	/**
	 * Example usage.
	 * @return usage
	 */
	public String usage();
	
	/**
	 * Result of the usage.
	 * @return result
	 */
	public String result();
}
