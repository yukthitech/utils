package com.yukthitech.utils.expr;

/**
 * Provides type of the specified variable.
 * @author akiran
 */
public interface IVariableTypeProvider
{
	/**
	 * Provides type of specified variable.
	 * @param name Name of the variable
	 * @return Variable type
	 */
	public Class<?> getVariableType(String name);
}
