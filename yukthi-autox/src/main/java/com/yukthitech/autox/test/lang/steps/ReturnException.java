package com.yukthitech.autox.test.lang.steps;

/**
 * Exception that will be thrown to break current loop.
 * @author akiran
 */
public class ReturnException extends LangException
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Return value.
	 */
	private Object value;

	public ReturnException(Object value)
	{
		this.value = value;
	}
	
	/**
	 * Gets the return value.
	 *
	 * @return the return value
	 */
	public Object getValue()
	{
		return value;
	}
}
