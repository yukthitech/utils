package com.yukthitech.persistence.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark functions which are used to add relations. Relation function should have
 * parent entity and child entity(ies) to be added.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface MultiRelationFunction
{
	/**
	 * Name of the relation (name of the field which indicates the relation).
	 * @return name of the relation.
	 */
	public String value();
	
	/**
	 * Operation to be performed.
	 * @return Operation to be performed.
	 */
	public RelationOp op() default RelationOp.ADD;
}
