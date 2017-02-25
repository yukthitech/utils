package com.yukthitech.persistence.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to bind result field to entity field in read queries. And also used to 
 * bind the field to update in update queries.
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface Field
{
	/**
	 * Name of the entity field
	 * @return name of the entity field
	 */
	public String value();
	
	/**
	 * Update operator to be used on target entity field value in conjunction with current field value.
	 * By default {@link UpdateOperator#NONE} is used, which would simply set current field value to the
	 * target entity field.
	 * @return Update operator to be used
	 */
	public UpdateOperator updateOp() default UpdateOperator.NONE;
}
