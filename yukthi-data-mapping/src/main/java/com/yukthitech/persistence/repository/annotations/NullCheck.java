package com.yukthitech.persistence.repository.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to null conditions to target query method
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NullCheck
{
	/**
	 * Field for which null condition has to be checked. 
	 * @return field to check for null
	 */
	public String field();
	
	/**
	 *  By default false. If made true checks for IS NOT NULL condition on specified fields.
	 * @return Flag indicating whether null or not-null should be checked
	 */
	public boolean checkForNotNull() default false;
	
	/**
	 * Join operator which would be used to join this condition to other conditions
	 * @return jooin operator
	 */
	public JoinOperator joinOperator() default JoinOperator.AND;
}
