package com.yukthitech.utils.expr;

/**
 * Represents evaluatable operator.
 * @author akiran
 */
public interface IOperator
{
	/**
	 * Gets operator as string.
	 * @return operator string.
	 */
	public String getOperator();
	
	/**
	 * Fetches description of the operator.
	 * @return Description
	 */
	public String getDescription();
	
	/**
	 * Return type of the operator for specified operand types.
	 * @param leftOperand left operand type
	 * @param rightOperand right operand type
	 * @return Return type
	 */
	public Class<?> getReturnType(Class<?> leftOperand, Class<?> rightOperand);
	
	/**
	 * Evaluates the operator with specified operand values.
	 * @param leftOperand Left operand value
	 * @param rightOperand right operand value
	 * @return Result of operator
	 */
	public Object evaluate(Object leftOperand, Object rightOperand);
}
