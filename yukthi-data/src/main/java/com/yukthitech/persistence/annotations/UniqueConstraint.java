package com.yukthitech.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents unique constraint
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface UniqueConstraint
{
	/**
	 * Name of the constraint
	 * @return
	 */
	public String name();
	
	/**
	 * Fields on which unique constraint needs to be maintained
	 * @return
	 */
	public String[] fields() default {};
	
	/**
	 * Error message to be used when constraint fails
	 * @return
	 */
	public String message() default "";
	
	/**
	 * Flag to indicate whether this constraint has to be validated
	 * before insert or update
	 * @return
	 */
	public boolean validate() default true;
	
	/**
	 * Indicates that specified constraint name is final name. And framework 
	 * should not prefix or suffix with anything else. Default: false.
	 * @return true if specified name is final one.
	 */
	public boolean finalName() default false;
}
