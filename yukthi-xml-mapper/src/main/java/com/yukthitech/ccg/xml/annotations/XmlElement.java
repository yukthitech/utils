package com.yukthitech.ccg.xml.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a property to be saved as element only.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface XmlElement
{
	/**
	 * To be specified if custom name has to be used.
	 * @return name of element.
	 */
	public String name() default "";
	
	/**
	 * If marked true, element value will be saved in cdata section.
	 * @return true if element value should be stored as cdata.
	 */
	public boolean cdata() default false;
}
