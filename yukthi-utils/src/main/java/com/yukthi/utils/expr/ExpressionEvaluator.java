package com.yukthi.utils.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Parser for parsing expression string into expressions.
 * @author akiran
 */
public class ExpressionEvaluator
{
	/**
	 * Parses expression string into expression.
	 * @param expressionStr Expression string to be parsed.
	 * @return Expression object
	 */
	public Expression parse(String expressionStr)
	{
		if(expressionStr == null || expressionStr.trim().length() == 0)
		{
			throw new NullPointerException("Null/empty expression string provided.");
		}
		
		//divide string into tokens
		List<Token> tokenLst = tokenize(expressionStr);
		
		//convert tokens into RPN sequence
		List<Token> rpnFormat = toRpnExpression(tokenLst);

		Object rpnTokens[] = rpnFormat.toArray();
		
		if(rpnTokens.length == 1)
		{
			Token token = (Token)rpnTokens[0];
			
			if(token.tokenType == TokenType.LITERAL)
			{
				return new Expression(new Literal(token.string));	
			}
			
			if(token.tokenType == TokenType.NUMBER)
			{
				return new Expression(new Literal(Double.parseDouble(token.string)));	
			}

			if(token.tokenType == TokenType.STRING)
			{
				return new Expression(new Variable(token.string));	
			}
			
			throw new ParseException("Invalid single-token encountered while parsing expression - {}", expressionStr);
		}
		
		List<Object> operands = new ArrayList<Object>();
		int index = 0;
		IExpressionPart finalExpression = null;
		
		//build the expression from tokens
		while(index < rpnTokens.length)
		{
			if(rpnTokens[index] == null)
			{
				continue;
			}
			
			if(rpnTokens[index] instanceof Token)
			{
				if( ((Token) rpnTokens[index]).tokenType == TokenType.OPERATOR)
				{
					fetchOperands(rpnTokens, index, 2, operands);
					
					rpnTokens[index] = new OperatorExpr((IExpressionPart) operands.get(1), 
							((Token) rpnTokens[index]).string, (IExpressionPart) operands.get(0));
					
					finalExpression = (IExpressionPart) rpnTokens[index];
				}
				else if( ((Token) rpnTokens[index]).tokenType == TokenType.FUNC_NAME)
				{
					fetchOperands(rpnTokens, index, -1, operands);
					Collections.reverse(operands);
					
					rpnTokens[index] = new FunctionExpr( ((Token) rpnTokens[index]).string , operands.toArray(new IExpressionPart[0]));
					finalExpression = (IExpressionPart) rpnTokens[index];
				}
			}
			
			index++;
		}
		
		if(finalExpression == null)
		{
			throw new ParseException("Failed to parse expression string into expression - {}", expressionStr);
		}
		
		return new Expression(finalExpression);
	}
	
	/**
	 * Fetches operand expressions from specified index.
	 * @param rpnTokens Expression tokens
	 * @param index Index from which operands needs to be fetched
	 * @param operandCount How many operands are expected. For functions, operands are paramters.
	 * @param operands List in which operands are to be collected
	 */
	private void fetchOperands(Object rpnTokens[], int index, int operandCount, List<Object> operands)
	{
		int j = index - 1;
		operands.clear();
		Token token = null;
		
		while(j >= 0)
		{
			//if open bracket is found
			if(rpnTokens[j] instanceof Token)
			{
				token = (Token) rpnTokens[j];
				
				if(token.tokenType == TokenType.OPEN_BRACKET)
				{
					//for functions consider open bracket as limit for parameters
					if(operandCount < 0)
					{
						rpnTokens[j] = null;
						break;
					}
					
					//for non-function cases ignore is completely
					continue;
				}
			}
			
			if(rpnTokens[j] != null)
			{
				operands.add( toExpression(rpnTokens[j]) );
				rpnTokens[j] = null;
				
				if(operandCount > 0 && operands.size() == operandCount)
				{
					break;
				}
			}
			
			j--;
		}
		
		if(operands.size() < operandCount)
		{
			throw new ParseException("Required number of operands not found for function or opertor at index - {}", index);
		}
	}
	
	/**
	 * Converts specified operand into corresponding expression part
	 * @param operand
	 * @return
	 */
	private IExpressionPart toExpression(Object operand)
	{
		if(operand instanceof IExpressionPart)
		{
			return (IExpressionPart) operand;
		}
		
		Token token = (Token) operand;
		
		if(token.tokenType == TokenType.LITERAL)
		{
			return new Literal(token.string);
		}
		else if(token.tokenType == TokenType.NUMBER || token.tokenType == TokenType.STRING)
		{
			Double numValue = null;
			
			try
			{
				numValue = Double.parseDouble(token.string);
				return new Literal(numValue);
			} catch(Exception ex)
			{
				return new Variable(token.string);
			}
		}
		else
		{
			throw new ParseException("Unsupported token type '{}' encountered as operand at index {}. Token - {}", 
					token.tokenType, token.start, token.string);
		}
	}
	
	/**
	 * Converts tokens of the expression into RPN format using "shunting-yard algorithm". Below is the algorithm details.
	 * While there are tokens to be read:
	 *   Read a token.
	 *   If the token is a number, then add it to the output queue.
	 *   If the token is a function token, then push it onto the stack.
	 *   If the token is a function argument separator (e.g., a comma):
	 *     Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the output queue. If no left parentheses are encountered, either the separator was misplaced or parentheses were mismatched.
	 *   If the token is an operator, o1, then:
	 *     while there is an operator token o2, at the top of the operator stack and either
	 *         o1 is left-associative and its precedence is less than or equal to that of o2, or
	 *         o1 is right associative, and has precedence less than that of o2,
	 *         pop o2 off the operator stack, onto the output queue;
	 *     at the end of iteration push o1 onto the operator stack.
	 *   If the token is a left parenthesis (i.e. "("), then push it onto the stack.
	 *   If the token is a right parenthesis (i.e. ")"):
	 *     Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the output queue.
	 *     Pop the left parenthesis from the stack, but not onto the output queue.
	 *     If the token at the top of the stack is a function token, pop it onto the output queue.
	 *     If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
	 * When there are no more tokens to read:
	 *   While there are still operator tokens in the stack:
	 *     If the operator token on the top of the stack is a parenthesis, then there are mismatched parentheses.
	 *     Pop the operator onto the output queue.
	 * Exit.
	 *  
	 * @param tokenLst Token list to be converted
	 * @return Tokens in RPN format
	 */
	List<Token> toRpnExpression(List<Token> tokenLst)
	{
		Token tokens[] = tokenLst.toArray(new Token[0]);
		Token token = null, subtoken = null;
		
		LinkedList<Token> outputQueue = new LinkedList<Token>();
		Stack<Token> stack = new Stack<Token>();
		int maxLen = tokens.length - 1;
		boolean found = false;
		
		for(int i = 0; i <= maxLen; i++)
		{
			token = tokens[i];
			
			if(token.tokenType == TokenType.NUMBER || token.tokenType == TokenType.STRING || token.tokenType == TokenType.LITERAL)
			{
				if(i < maxLen && tokens[i + 1].tokenType == TokenType.OPEN_BRACKET)
				{
					token.tokenType = TokenType.FUNC_NAME;
					
					//open bracket is added to the queue to identify limit of function parameters
					outputQueue.add(tokens[i + 1]);

					stack.push(token);
					continue;
				}
				
				outputQueue.add(token);
			}
			else if(token.tokenType == TokenType.COMMA)
			{
				found = false;
				
				//pop operators to output queue till open bracket is found
				while(!stack.isEmpty())
				{
					if(stack.peek().tokenType == TokenType.OPEN_BRACKET)
					{
						found = true;
						break;
					}
					
					outputQueue.add(stack.pop());
				}
				
				//if open bracket is not found on stack
				if(!found)
				{
					throw new ParseException("Comma encountered at {} in non-function context", token.start);
				}
			}
			else if(token.tokenType == TokenType.OPERATOR)
			{
				//pop operators to queue till operator of same or low level is found
				while(!stack.isEmpty())
				{
					//As operator precedence is not considered, this loop will continue
					//	which generally should be single operator
					if(stack.peek().tokenType != TokenType.OPERATOR)
					{
						break;
					}
					
					outputQueue.add(stack.pop());
				}
				
				stack.push(token);
			}
			else if(token.tokenType == TokenType.OPEN_BRACKET)
			{
				stack.push(token);
			}
			else if(token.tokenType == TokenType.CLOSE_BRACKET)
			{
				found = false;

				//pop operators to queue till open bracket is found
				while(!stack.isEmpty())
				{
					subtoken = stack.pop();
					
					if(subtoken.tokenType == TokenType.OPEN_BRACKET)
					{
						found = true;
						break;
					}
					
					outputQueue.add(subtoken);
				}
				
				if(!found)
				{
					throw new ParseException("No matching open bracket found closing bracket at index - {}", token.start);
				}
				
				if(!stack.isEmpty() && stack.peek().tokenType == TokenType.FUNC_NAME)
				{
					outputQueue.add(stack.pop());
				}
			}
			else
			{
				throw new ParseException("Invalid token type '{}' encountered for token '{}' at index - {}", token.tokenType, token.string, token.start);
			}
		}
		
		while(!stack.empty())
		{
			token = stack.peek();
			
			if(token.tokenType == TokenType.OPERATOR)
			{
				outputQueue.add(stack.pop());
				continue;
			}
			
			throw new ParseException("No matching close token found at index {}. Token - {}", token.start, token.string);
		}
		
		return outputQueue;
	}
	
	/**
	 * By ignoring white spaces parses the specified string into string tokens.
	 * @param expressionStr String to be parsed
	 * @return Tokenized string
	 */
	List<Token> tokenize(String expressionStr)
	{
		ParseState parseState = new ParseState();
		parseState.ch = expressionStr.toCharArray();
		char ch = 0;
		
		for(; parseState.index < parseState.ch.length; parseState.index++)
		{
			ch = parseState.currentChar();
			
			if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || ch == '_')
			{
				parseState.pushChar(ch, TokenType.STRING);
			}
			else if(ch >= '0' && ch <= '9')
			{
				parseState.pushChar(ch, TokenType.NUMBER);
			}
			else if(ch == '.')
			{
				if(parseState.curTokenType == TokenType.STRING || parseState.curTokenType == TokenType.NUMBER)
				{
					parseState.pushChar(ch, TokenType.NUMBER);
					continue;
				}
				
				throw new ParseException("Dot(.) encountered at unexpected position. Index: {}", parseState.index);
			}
			else if(ch == '(')
			{
				parseState.bracketOpened();
			}
			else if(ch == ')')
			{
				if(parseState.openBracketCount <= 0)
				{
					throw new ParseException("Bracket close encountered at unexpected position. Index: {}", parseState.index);
				}
				
				parseState.bracketClosed();
			}
			else if(ch == ',')
			{
				parseState.pushToken(',', TokenType.COMMA);
			}
			else if(ch == '\'' || ch == '\"')
			{
				parseState.extractStringLiteral();
			}
			else if(Character.isWhitespace(ch))
			{
				parseState.finalizeToken();
				continue;
			}
			else
			{
				parseState.pushChar(ch, TokenType.OPERATOR);
			}
		}
		
		parseState.finalizeToken();
		
		if(parseState.openBracketCount > 0)
		{
			throw new ParseException("Brackets are not closed properly");
		}
		
		return parseState.tokens;
	}
}
