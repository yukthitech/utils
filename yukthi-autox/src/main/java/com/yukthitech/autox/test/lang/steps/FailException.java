package com.yukthitech.autox.test.lang.steps;

/**
 * Exception that will be thrown to break current loop.
 * @author akiran
 */
public class FailException extends LangException
{
	private static final long serialVersionUID = 1L;

	public FailException()
	{
		super();
	}

	public FailException(String message)
	{
		super(message);
	}
}
