package com.yukthitech.ccg.xml.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a collection property to be saved using specified name (instead of <element> tags).
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface CollectionElement
{
	/**
	 * Collection element name to be used.
	 * @return name to be used
	 */
	public String value();
}
