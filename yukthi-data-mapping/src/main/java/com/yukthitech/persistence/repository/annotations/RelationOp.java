package com.yukthitech.persistence.repository.annotations;

/**
 * Enumeration of relation operations that can be performed.
 * @author akiran
 */
public enum RelationOp
{
	/**
	 * Adds specified relations.
	 */
	ADD,
	
	/**
	 * Removes specified relations.
	 */
	REMOVE,
	
	/**
	 * Retains only specified relations.
	 */
	RETAIN;
}
