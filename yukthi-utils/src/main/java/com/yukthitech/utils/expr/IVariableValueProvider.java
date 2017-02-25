package com.yukthitech.utils.expr;

/**
 * Provides value of the specified variable.
 * @author akiran
 */
public interface IVariableValueProvider
{
	/**
	 * Provides value of specified variable.
	 * @param name Name of the variable
	 * @return Variable value
	 */
	public Object getVariableValue(String name);
}
