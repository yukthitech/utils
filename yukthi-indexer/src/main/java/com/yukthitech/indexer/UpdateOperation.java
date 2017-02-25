package com.yukthitech.indexer;

/**
 * Update operation that can be performed on the field being updated.
 * @author akiran
 */
public enum UpdateOperation
{
	REPLACE("="), 
	APPEND("+="),
	ADD("+="), 
	SUBTRACT("-="), 
	MULTIPLY("*="), 
	DIVIDE("/=");
	
	private String operator;

	private UpdateOperation(String operator)
	{
		this.operator = operator;
	}
	
	public String getOperator()
	{
		return operator;
	}
}
