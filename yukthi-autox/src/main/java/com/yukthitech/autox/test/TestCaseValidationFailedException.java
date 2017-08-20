package com.yukthitech.autox.test;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Exception to be thrown when validation fails.
 * @author akiran
 */
public class TestCaseValidationFailedException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	public TestCaseValidationFailedException(String message, Object... args)
	{
		super(message, args);
	}
}
