package com.yukthitech.autox.ide.exeenv;

/**
 * Types of environment event.
 * @author akiran
 */
public enum EnvironmentEventType
{
	/**
	 * New content is added to console.
	 */
	CONSOLE_CHANGED,
	
	/**
	 * New log entry is added to reports.
	 */
	REPORT_LOG_ADDED,
	
	/**
	 * New context attribute is added to ContextAttributesPanel 
	 */
	CONTEXT_ATTRIBUTE_CHANGED
	
	;
}
