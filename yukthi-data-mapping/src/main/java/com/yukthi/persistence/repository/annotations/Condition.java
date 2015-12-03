package com.yukthi.persistence.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the target parameter or field as condition
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface Condition
{
	/**
	 * Name of the entity field on which this condition should be applied. If the target is
	 * finder method param, this is mandatory. If this annotation is used on field, this is
	 * optional.
	 * @return Entity field name expression
	 */
	public String value() default "";
	
	/**
	 * Operator to be used in condition
	 * @return Condition operator to be used
	 */
	public Operator op() default Operator.EQ;
	
	/**
	 * Join operator to be used for this condition
	 * @return Join operator to be used for this condition
	 */
	public JoinOperator joinWith() default JoinOperator.AND;
	
	/**
	 * Indicates this condition can hold null values
	 * @return Whether this condition can hold null values
	 */
	public boolean nullable() default false;
	
	/**
	 * During condition evaluation case will be ignored
	 * @return
	 */
	public boolean ignoreCase() default false;
}
