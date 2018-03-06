package com.yukthitech.autox.test;

import com.yukthitech.autox.IStep;

public class TestCaseFailedException extends AutoxException
{
	private static final long serialVersionUID = 1L;

	public TestCaseFailedException(IStep step, String message, Object... args)
	{
		super(step, message, args);
	}
}
