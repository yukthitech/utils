package com.yukthitech.ccg.xml.reserved;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides the node information on which target class can act.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NodeName
{
	/**
	 * Node name pattern to which target handler should be applied.
	 * @return
	 */
	public String namePattern();
}
