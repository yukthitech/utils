package com.yukthitech.autox.test;

import com.yukthitech.autox.IStep;

/**
 * Exception to be thrown when validation fails.
 * @author akiran
 */
public class TestCaseValidationFailedException extends AutoxException
{
	private static final long serialVersionUID = 1L;

	public TestCaseValidationFailedException(IStep step, String message, Object... args)
	{
		super(step, message, args);
	}
}
