package com.yukthitech.autox.debug.common;

/**
 * Enumeration of debug operations.
 * @author akranthikiran
 */
public enum DebugOp
{
	/**
	 * Gets inside the function or next line.
	 */
	STEP_INTO,
	
	/**
	 * Goes to next line.
	 */
	STEP_OVER,
	
	/**
	 * Goes to next debug point.
	 */
	STEP_RETURN
}
