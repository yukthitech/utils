package com.yukthitech.excel.importer.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to define the heading and other configurations for excel column.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelLabel
{
	public String value();
	
	/**
	 * Format to be used to convert Date/Number values.
	 */
	public String format() default "";
}
