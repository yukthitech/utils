package com.yukthi.utils.expr;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an expression.
 * @author akiran
 */
public class Expression
{
	/**
	 * Expression part which represents the actual expression.
	 */
	private IExpressionPart expressionPart;

	/**
	 * Instantiates a new expression.
	 *
	 * @param expressionPart the expression part
	 */
	public Expression(IExpressionPart expressionPart)
	{
		this.expressionPart = expressionPart;
	}
	
	/**
	 * Gets the main expression part.
	 * @return Main expression part.
	 */
	public IExpressionPart getExpressionPart()
	{
		return expressionPart;
	}
	
	/**
	 * Fetches the variable names used in this expression.
	 * @return Variable names used.
	 */
	public Set<String> getVariableNames()
	{
		Set<String> variableNames = new HashSet<String>();
		expressionPart.collectVariables(variableNames);
		
		return variableNames;
	}
	
	/**
	 * Fetches the expression result type.
	 * @param variableTypeProvider Variable type provider
	 * @param registry Expression registry to be used
	 * @return Final result type.
	 */
	public Class<?> getExpressionType(IVariableTypeProvider variableTypeProvider, ExpressionRegistry registry)
	{
		return expressionPart.getType(variableTypeProvider, registry);
	}
	
	/**
	 * Evaluates the expression and provides the result.
	 * @param variableValueProvider Variable value provider
	 * @param registry Expression registry to be used
	 * @return Final value
	 */
	public Object evaluate(IVariableValueProvider variableValueProvider, ExpressionRegistry registry)
	{
		return expressionPart.evaluate(variableValueProvider, registry);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return expressionPart.toString();
	}
}
