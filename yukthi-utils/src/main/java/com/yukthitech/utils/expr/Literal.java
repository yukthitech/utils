package com.yukthitech.utils.expr;

import java.util.Set;

/**
 * Represents literal value in expression.
 * @author akiran
 */
public class Literal implements IExpressionPart
{
	/**
	 * Literal value.
	 */
	private Object value;

	/**
	 * Instantiates a new literal.
	 *
	 * @param value the value
	 */
	public Literal(Object value)
	{
		this.value = value;
	}
	
	/**
	 * Gets the literal value.
	 *
	 * @return the literal value
	 */
	public Object getValue()
	{
		return value;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IExpressionPart#collectVariables(java.util.Set)
	 */
	@Override
	public void collectVariables(Set<String> variables)
	{
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IExpressionPart#getType(com.yukthitech.webutils.utils.expr.IVariableTypeProvider, com.yukthitech.webutils.utils.expr.ExpressionRegistry)
	 */
	@Override
	public Class<?> getType(IVariableTypeProvider variableTypeProvider, ExpressionRegistry registry)
	{
		return value.getClass();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IExpressionPart#evaluate(com.yukthitech.webutils.utils.expr.IVariableValueProvider, com.yukthitech.webutils.utils.expr.ExpressionRegistry)
	 */
	@Override
	public Object evaluate(IVariableValueProvider variableValueProvider, ExpressionRegistry registry)
	{
		return value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "" + value;
	}
}
