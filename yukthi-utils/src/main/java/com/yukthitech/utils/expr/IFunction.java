package com.yukthitech.utils.expr;

/**
 * Represents function that can be used in expressions.
 * @author akiran
 */
public interface IFunction
{
	/**
	 * Name of the function.
	 * @return name of the function.
	 */
	public String getName();
	
	/**
	 * Syntax of the function.
	 * @return syntax of the function.
	 */
	public String getSyntax();
	
	/**
	 * Description of the function.
	 * @return Description of the function.
	 */
	public String getDescription();
	
	/**
	 * Fetches the would be return type based on parameter types specified.
	 * @param paramTypes Parameter types.
	 * @return Would be return type based on parameter types.
	 */
	public Class<?> getReturnType(Class<?> paramTypes[]);
	
	/**
	 * Evaluates current function with specified parameter values.
	 * @param parameters Parameter values.
	 * @return Result value.
	 */
	public Object evaluate(Object parameters[]);
}
