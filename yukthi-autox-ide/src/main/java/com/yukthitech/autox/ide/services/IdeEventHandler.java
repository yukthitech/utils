package com.yukthitech.autox.ide.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark target method as handler of ide events. 
 * This should accept any of the implementation {@link IIdeEvent} as argument. Only for that event handling
 * target method will be invoked.
 * 
 * @author akranthikiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IdeEventHandler
{
}
