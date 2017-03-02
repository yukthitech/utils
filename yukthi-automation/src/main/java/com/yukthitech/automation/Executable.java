package com.yukthitech.automation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.automation.config.IConfiguration;

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
	 * Configuration type required by current executable (step or validator)
	 * @return required configuration type.
	 */
	public Class<? extends IConfiguration<?>>[] requiredConfigurationTypes() default {};
}
