package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.IStep;

/**
 * Exception that will be thrown to break current loop.
 * @author akiran
 */
public class FailException extends AutoxValidationException
{
	private static final long serialVersionUID = 1L;

	public FailException(IStep step)
	{
		super(step, null);
	}

	public FailException(IStep step, String message)
	{
		super(step, message);
	}
}
