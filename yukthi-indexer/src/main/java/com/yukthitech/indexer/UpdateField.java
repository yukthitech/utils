package com.yukthitech.indexer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Hints on how the field should be used for object indexing.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UpdateField
{
	/**
	 * Name of the index field to update. Defaults to current field.
	 * @return
	 */
	public String name() default "";

	/**
	 * Update operation to be performed. Defaults to replace.
	 * @return
	 */
	public UpdateOperation op() default UpdateOperation.REPLACE;
}
