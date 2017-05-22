package com.yukthitech.autox.doc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to give description of free marker method parameter.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FmParam
{
	/**
	 * Name of the parameter.
	 * @return
	 */
	public String name();
	
	/**
	 * Description of the parameter.
	 * @return
	 */
	public String description();
}
