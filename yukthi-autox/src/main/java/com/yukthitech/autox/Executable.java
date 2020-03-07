package com.yukthitech.autox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.autox.config.IPlugin;

/**
 * Used to mark a step/validation as executable, so that they can be found dynamically.
 * @author akiran
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Executable
{
	/**
	 * Provides name of executable.
	 * @return Executable name.
	 */
	public String name();
	
	/**
	 * Message representing the target.
	 * @return Message
	 */
	public String message();
	
	/**
	 * Plugin types required by current executable (step or validator)
	 * @return required plugin type.
	 */
	public Class<? extends IPlugin<?>>[] requiredPluginTypes() default {};
	
	/**
	 * Group to which current executable belongs to. 
	 * @return
	 */
	public Group group();
}
