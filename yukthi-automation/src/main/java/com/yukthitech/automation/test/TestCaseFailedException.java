package com.yukthitech.automation.test;

import com.yukthitech.utils.exceptions.UtilsException;

public class TestCaseFailedException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	public TestCaseFailedException(String message, Object... args)
	{
		super(message, args);
	}
}
