package com.yukthi.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a field as a holder of extended fields. This field name will be used as prefix in search 
 * or finder queries to fetch extension fields.
 * @author akiran
 */
@Transient
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface ExtendedFields
{
}
