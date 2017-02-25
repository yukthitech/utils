package com.yukthitech.persistence.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify more details in ordering fields.
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface OrderByField
{
	/**
	 * Name of the field.
	 * @return Name of the field
	 */
	public String name();
	
	/**
	 * Type of ordering.
	 * @return type of ordering.
	 */
	public OrderByType type() default OrderByType.ASC;
}
