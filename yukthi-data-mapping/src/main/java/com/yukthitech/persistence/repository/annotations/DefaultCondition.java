package com.yukthitech.persistence.repository.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to null conditions to target query method
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultCondition
{
	/**
	 * Field for which null condition has to be checked. 
	 * @return field to check for null
	 */
	public String field();
	
	/**
	 * Operator to be used in condition
	 * @return Condition operator to be used
	 */
	public Operator op() default Operator.EQ;

	/**
	 * Value for the condition
	 * @return Value for the condition
	 */
	public String value();
	
	/**
	 * Join operator which would be used to join this condition to other conditions
	 * @return jooin operator
	 */
	public JoinOperator joinOperator() default JoinOperator.AND;
}
