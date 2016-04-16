package com.yukthi.utils.expr;

/**
 * Simple abstract implementation of operator where operand types and return type is fixed.
 * @author akiran
 */
public abstract class SimpleOperator implements IOperator
{
	/**
	 * Operator string representation.
	 */
	private String operatorString;
	
	/**
	 * Operator description.
	 */
	private String description;
	
	/**
	 * Support left operand type.
	 */
	private Class<?> leftOperandType;
	
	/**
	 * Supported right operand type.
	 */
	private Class<?> rightOperandType;
	
	/**
	 * Result type of operator execution.
	 */
	private Class<?> resultType;

	/**
	 * Instantiates a new simple operator.
	 *
	 * @param operatorString the operator string
	 * @param description the description
	 * @param leftOperandType the left operand type
	 * @param rightOperandType the right operand type
	 * @param resultType the result type
	 */
	public SimpleOperator(String operatorString, String description, Class<?> leftOperandType, Class<?> rightOperandType, Class<?> resultType)
	{
		this.operatorString = operatorString;
		this.description = description;
		this.leftOperandType = leftOperandType;
		this.rightOperandType = rightOperandType;
		this.resultType = resultType;
	}
	
	/**
	 * Instantiates a new simple operator.
	 *
	 * @param operatorString the operator string
	 * @param description the description
	 * @param operandsType the operands type
	 * @param resultType the result type
	 */
	public SimpleOperator(String operatorString, String description, Class<?> operandsType, Class<?> resultType)
	{
		this(operatorString, description, operandsType, operandsType, resultType);
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.utils.expr.IOperator#getReturnType(java.lang.Class, java.lang.Class)
	 */
	@Override
	public Class<?> getReturnType(Class<?> leftOperand, Class<?> rightOperand)
	{
		if(!this.leftOperandType.isAssignableFrom(leftOperand))
		{
			throw new InvalidTypeException("Invalid left operand type {} encountered for operator {}", leftOperand, operatorString);
		}
		
		if(!this.rightOperandType.isAssignableFrom(rightOperand))
		{
			throw new InvalidTypeException("Invalid right operand type {} encountered for operator {}", rightOperand, operatorString);
		}
		
		return resultType;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.utils.expr.IOperator#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.utils.expr.IOperator#getOperator()
	 */
	@Override
	public String getOperator()
	{
		return operatorString;
	}
}
