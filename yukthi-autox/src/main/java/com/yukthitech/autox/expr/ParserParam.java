package com.yukthitech.autox.expr;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to document parser parameters.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ParserParam
{
	/**
	 * Name of the parameter.
	 * @return name
	 */
	public String name();
	
	/**
	 * Type of parameter.
	 * @return type
	 */
	public String type();
	
	/**
	 * Default value of the parameter.
	 * @return default value
	 */
	public String defaultValue();
	
	/**
	 * Description of the parameter.
	 * @return description.
	 */
	public String description();
}
