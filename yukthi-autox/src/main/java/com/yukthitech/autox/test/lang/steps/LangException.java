package com.yukthitech.autox.test.lang.steps;

/**
 * Base exceptions for lang exceptions.
 * @author akiran
 */
public class LangException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public LangException()
	{
	}

	public LangException(String message)
	{
		super(message);
	}
}
