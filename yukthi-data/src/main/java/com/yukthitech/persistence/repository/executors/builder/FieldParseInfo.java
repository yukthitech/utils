package com.yukthitech.persistence.repository.executors.builder;

import com.yukthitech.persistence.FieldDetails;

/**
 * Intermediate table used while parsing expressions.
 * @author akiran
 */
class FieldParseInfo
{
	/**
	 * Field or parameter (in query or result bean/parameter) whose expression
	 * is being parsed.
	 */
	public String sourceField;
	
	/**
	 * Expression to be parsed.
	 */
	public String expression;
	
	/**
	 * Entity field path parts.
	 */
	public String entityFieldPath[];
	
	/**
	 * Description of the repository method.
	 */
	public String methodDesc;
	
	/**
	 * Final field details represented by this expression.
	 */
	public FieldDetails fieldDetails;
	
	/**
	 * Table on which this field was found.
	 */
	public TableInfo tableInfo;

	public FieldParseInfo(String sourceField, String expression, String methodDesc)
	{
		this.sourceField = sourceField;
		this.expression = expression;
		this.entityFieldPath = expression.split("\\s*\\.\\s*");
		this.methodDesc = methodDesc;
	}
}
