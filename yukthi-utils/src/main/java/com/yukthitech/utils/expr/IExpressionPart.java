package com.yukthitech.utils.expr;

import java.util.Set;

/**
 * Represents abstract of part of the expression.
 * @author akiran
 */
public interface IExpressionPart
{
	/**
	 * Collects variables used in this part into provided "variables".
	 * @param variables Set where variables will be collected.
	 */
	public void collectVariables(Set<String> variables);
	
	/**
	 * Gets the result type of the expression part.
	 * @param variableTypeProvider Provides variable type.
	 * @param registry Expression registry to be used
	 * @return Final result type
	 */
	public Class<?> getType(IVariableTypeProvider variableTypeProvider, ExpressionRegistry registry);
	
	/**
	 * Evaluates expression part and provides final value.
	 * @param variableValueProvider Variable value provider.
	 * @param registry Expression registry to be used
	 * @return Result value
	 */
	public Object evaluate(IVariableValueProvider variableValueProvider, ExpressionRegistry registry);
}
