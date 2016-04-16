package com.yukthi.utils.expr;

/**
 * Expression token type.
 * @author akiran
 */
enum TokenType
{
	STRING, OPERATOR, NUMBER, OPEN_BRACKET, CLOSE_BRACKET, 
	
	/**
	 * Used for constant values. Ex: 2, 4.5, 'dfd', "dff fgg" 
	 */
	LITERAL, 
	
	COMMA, FUNC_NAME;
}
