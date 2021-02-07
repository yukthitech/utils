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
public @interface IndexField
{
	/**
	 * Indicates how indexing should be done for this field.
	 * @return
	 */
	public IndexType value() default IndexType.NOT_ANALYZED;
	
	/**
	 * If true, this field will be used id field for the object.
	 * @return
	 */
	public boolean idField() default false;
}
