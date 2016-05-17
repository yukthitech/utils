package com.yukthi.indexer;

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
	public IndexType value() default IndexType.ANALYZED;
	
}
