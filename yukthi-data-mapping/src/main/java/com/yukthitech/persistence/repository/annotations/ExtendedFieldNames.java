package com.yukthitech.persistence.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the parameter needs to be used to fetch custom fields to be fetches.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ExtendedFieldNames
{
}
