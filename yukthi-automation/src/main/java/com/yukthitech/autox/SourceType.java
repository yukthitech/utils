package com.yukthitech.autox;

/**
 * Indicates the type of parameter.
 * @author akiran
 */
public enum SourceType
{
	/**
	 * Indicates param is resource.
	 */
	RESOURCE,
	
	/**
	 * Indicates param is object resource.
	 */
	OBJECT,
	
	/**
	 * Indicates param represents ui locator.
	 */
	UI_LOCATOR,
	
	/**
	 * Indicates param is normal type.
	 */
	NONE;
}
