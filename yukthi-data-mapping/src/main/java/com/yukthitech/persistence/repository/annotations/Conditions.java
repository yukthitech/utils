package com.yukthitech.persistence.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the target parameter or field for conditions. The conditions
 * in this annotation are grouped together.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface Conditions
{
	/**
	 * List of conditions to be grouped.
	 * @return 
	 */
	public Condition[] value();
	
	/**
	 * Join operator to be used for this condition
	 * @return Join operator to be used for this condition
	 */
	public JoinOperator joinWith() default JoinOperator.AND;
}
