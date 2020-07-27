package com.yukthitech.autox.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method as expression parser method.
 * @author akiran
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpressionFilter
{
	/**
	 * Provides expression type.
	 * @return expression type.
	 */
	public String type();
	
	/**
	 * Description of the parser.
	 * @return description
	 */
	public String description();

	/**
	 * Example.
	 * @return example
	 */
	public String example();
	
	/**
	 * Type of content expected.
	 * @return content type
	 */
	public ParserContentType contentType() default ParserContentType.NONE;
	
	/**
	 * Parameters supported by this parser.
	 * @return parameters
	 */
	public ParserParam[] params() default {};
}
