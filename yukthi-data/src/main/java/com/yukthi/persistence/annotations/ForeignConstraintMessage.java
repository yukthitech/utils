package com.yukthi.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify message for foreign constraint when it fails
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignConstraintMessage
{
	/**
	 * Message to be used when wrong foreign key value is used
	 * @return
	 */
	public String message();
	
	/**
	 * Message to be used when parent is getting deleted before child entities are deleted
	 * @return
	 */
	public String deleteMessage();
}
