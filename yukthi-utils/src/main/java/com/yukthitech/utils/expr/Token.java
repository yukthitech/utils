package com.yukthitech.utils.expr;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Represents a expression token in the expression being parsed.
 * @author akiran
 */
class Token
{
	/**
	 * Index where token is started.
	 */
	int start;
	
	/**
	 * Token value as string.
	 */
	String string;
	
	/**
	 * Type of token.
	 */
	TokenType tokenType;
	
	public Token(int start, String string, TokenType tokenType)
	{
		if(start < 0)
		{
			throw new InvalidArgumentException("Invalid index specified - {}", start);
		}
		
		this.start = start;
		this.string = string;
		this.tokenType = tokenType;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("{");

		builder.append("Idx: ").append(start);
		builder.append(",").append("String: ").append(string);
		builder.append(",").append("type: ").append(tokenType);

		builder.append("}");
		return builder.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof Token))
		{
			return false;
		}

		Token other = (Token) obj;
		return string.equals(other.string);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return string.hashCode();
	}
	
	/**
	 * Helper function to convert specified string into dummy tokens
	 * @param tokens
	 * @return
	 */
	public static List<Token> tokens(String... tokens)
	{
		List<Token> tokenLst = new ArrayList<Token>();
		
		for(String token : tokens)
		{
			tokenLst.add(new Token(0, token, TokenType.STRING));
		}
		
		return tokenLst;
	}
}
