package com.yukthitech.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate the current child entity should get deleted with parent. This should be
 * used in child entity on the field which specified (mapped) relation to parent.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DeleteWithParent
{
}
