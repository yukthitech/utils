package com.yukthi.utils.expr;

import java.util.Set;

/**
 * Represents variable in expression.
 * @author akiran
 */
public class Variable implements IExpressionPart
{
	/**
	 * Name of the variable.
	 */
	private String name;

	/**
	 * Instantiates a new variable.
	 *
	 * @param name the name
	 */
	public Variable(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the name of the variable.
	 *
	 * @return the name of the variable
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Collect variables.
	 *
	 * @param variables the variables
	 */
	@Override
	public void collectVariables(Set<String> variables)
	{
		variables.add(name);
	}

	/**
	 * Gets the type.
	 *
	 * @param variableTypeProvider the variable type provider
	 * @param registry the registry
	 * @return the type
	 */
	@Override
	public Class<?> getType(IVariableTypeProvider variableTypeProvider, ExpressionRegistry registry)
	{
		return variableTypeProvider.getVariableType(name);
	}

	/**
	 * Evaluate.
	 *
	 * @param variableValueProvider the variable value provider
	 * @param registry the registry
	 * @return the object
	 */
	@Override
	public Object evaluate(IVariableValueProvider variableValueProvider, ExpressionRegistry registry)
	{
		return variableValueProvider.getVariableValue(name);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "@" + name;
	}
}
