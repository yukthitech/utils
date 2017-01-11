package com.yukthi.utils.expr;

import java.util.LinkedList;
import java.util.List;

/**
 * Used to maintain the state of the expression parsing.
 * @author akiran
 */
class ParseState
{
	/**
	 * Tokens of the expression.
	 */
	List<Token> tokens = new LinkedList<Token>();
	
	/**
	 * Characters from expression string.
	 */
	char ch[];
	
	/**
	 * Current index where parsing is going on.
	 */
	int index;
	
	/**
	 * Used to hold current token being parsed.
	 */
	StringBuilder currentToken = new StringBuilder();
	
	/**
	 * Current token type.
	 */
	TokenType curTokenType = null;
	
	/**
	 * New token type.
	 */
	TokenType newTokenType = null;
	
	/**
	 * Number of unclosed brackets found till now.
	 */
	int openBracketCount = 0;
	
	/**
	 * Index where current token is started.
	 */
	int tokenStart = 0;
	
	/**
	 * Returns current char
	 * @return
	 */
	public char currentChar()
	{
		return ch[index];
	}
	
	/**
	 * Adds the current token to final list and resets the new token parameters.
	 */
	public void finalizeToken()
	{
		if(currentToken.length() <= 0)
		{
			return;
		}
		
		tokens.add(new Token(tokenStart, currentToken.toString(), curTokenType));
		currentToken.setLength(0);
		tokenStart = -1;
		curTokenType = null;
	}

	/**
	 * Pushes the specified character to the curren token
	 * @param ch
	 * @param newTokenType
	 */
	public void pushChar(char ch, TokenType newTokenType)
	{
		//if new char token type belong to current token type
		if(curTokenType == newTokenType)
		{
			currentToken.append(ch);
			return;
		}
		
		//if current type is string or numbet
		if(curTokenType == TokenType.STRING && newTokenType == TokenType.NUMBER)
		{
			currentToken.append(ch);
			return;
		}
		
		//for string and numbers accept dot
		if(ch == '.' && (curTokenType == TokenType.STRING || newTokenType == TokenType.NUMBER))
		{
			currentToken.append(ch);
			return;
		}
		
		//if new token is started, finalize current token and start new token
		finalizeToken();
		
		tokenStart = index;
		currentToken.append(ch);
		curTokenType = newTokenType;
	}
	
	public void bracketOpened()
	{
		pushToken('(', TokenType.OPEN_BRACKET);
		openBracketCount++;
	}
	
	public void bracketClosed()
	{
		pushToken(')', TokenType.CLOSE_BRACKET);
		openBracketCount--;
	}
	
	public void pushToken(char token, TokenType type)
	{
		finalizeToken();
		tokens.add(new Token(index, "" + token, type));
	}
	
	/**
	 * Extracts string literal adding all the character to string literal token till 
	 * literal token is closed. Takes care of escape chars.
	 */
	public void extractStringLiteral()
	{
		finalizeToken();
		
		char quoteType = ch[index];
		int startIdx = index;
		
		//skip opening quote
		index++;
		
		boolean closed = false;
		
		for(; index < ch.length; index++)
		{
			if(ch[index] == '\\')
			{
				index++;
				currentToken.append(ch[index]);
				continue;
			}
			
			if(ch[index] == quoteType)
			{
				closed = true;
				break;
			}
			
			currentToken.append(ch[index]);
		}
		
		if(!closed)
		{
			throw new ParseException("String literal started at {} is not ended.", startIdx);
		}
		
		tokens.add(new Token(startIdx, currentToken.toString(), TokenType.LITERAL));
		currentToken.setLength(0);
		curTokenType = null;
		tokenStart = -1;
	}
}
