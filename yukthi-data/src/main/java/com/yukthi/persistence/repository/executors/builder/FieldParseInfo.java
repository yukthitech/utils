package com.yukthi.persistence.repository.executors.builder;

import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.FieldDetails;

/**
 * Intermediate table used while parsing expressions.
 * @author akiran
 */
class FieldParseInfo
{
	/**
	 * Target entity details.
	 */
	public EntityDetails entityDetails;
	
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
	
}
