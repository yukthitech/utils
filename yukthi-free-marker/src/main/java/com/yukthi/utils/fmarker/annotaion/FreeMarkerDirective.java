package com.yukthi.utils.fmarker.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks target method as free marker directive.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FreeMarkerDirective
{
	/**
	 * Name using which target method can be accessed from free marker templates as directive.
	 * Defaults to target method name.
	 * @return
	 */
	public String value() default "";
}
