package com.yukthi.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthi.persistence.repository.annotations.Charset;

/**
 * Marks the target entity type as extendable and provides extension attributes.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Extendable
{
	/**
	 * Name of the table where extended fields should be maintained. By default, 
	 * they are maintained in table with name EXT_[entity-table-name].
	 * @return extended fields table name
	 */
	public String tableName() default "";
	
	/**
	 * Extended field prefix. By default it is "field", so fields will be named as field0, field1, field2... so on.
	 * @return Extended field prefix.
	 */
	public String fieldPrefix() default "field";
	
	/**
	 * Number of extension fields.
	 * @return number of extension fields.
	 */
	public int count();
	
	/**
	 * Size of each extension field. This will in turn defines the maximum size of the value in 
	 * an extension field.
	 * @return size of extension field.
	 */
	public int fieldSize();
	
	/**
	 * Character set for extension field data. Default is LATIN1.
	 * @return Character set to be used.
	 */
	public Charset charset() default Charset.LATIN1;
}
