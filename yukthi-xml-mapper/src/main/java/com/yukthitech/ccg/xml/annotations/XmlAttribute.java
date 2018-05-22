package com.yukthitech.ccg.xml.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a property to be saved as attribute only.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface XmlAttribute
{
	/**
	 * To be specified if custom name has to be used.
	 * @return name of attribute.
	 */
	public String name() default "";
}
