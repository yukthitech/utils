package com.yukthitech.autox.test.lang.steps;

/**
 * Exception that will be thrown to break current loop.
 * @author akiran
 */
public class ValuedException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	private Object value;

	public ValuedException(String message, Object value)
	{
		super(message);
		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}
}
