package com.yukthitech.persistence.repository.annotations;

public enum Operator
{
	EQ("=", true), 
	LT("<"), 
	LE("<="), 
	GT(">"), 
	GE(">="), 
	NE("!=", true), 
	LIKE("LIKE"), 
	IN("IN"), 
	NOT_IN("NOT IN"), 

	;
	private String operator;
	
	/**
	 * Indicates whether operator supports null values
	 */
	private boolean nullable;
	
	private Operator(String operator)
	{
		this.operator = operator;
	}

	private Operator(String operator, boolean nullable)
	{
		this.operator = operator;
		this.nullable = nullable;
	}
	
	public boolean isNullable()
	{
		return nullable;
	}
	
	public String getOperator()
	{
		return operator;
	}

	@Override
	public String toString()
	{
		return operator;
	}
	
	
}
