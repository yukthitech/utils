package com.yukthitech.autox.test;

/**
 * Test case execution status.
 * @author akiran
 */
public enum TestStatus
{
	/**
	 * Represents success state of test case.
	 */
	SUCCESSFUL(false),
	
	/**
	 * Indicates test case failed.
	 */
	FAILED(true),
	
	/**
	 * Indicates test case is skipped.
	 */
	SKIPPED(false),
	
	/**
	 * Indicates an error occurred while test case execution.
	 */
	ERRORED(true);
	
	private boolean errored;

	private TestStatus(boolean errored)
	{
		this.errored = errored;
	}
	
	public boolean isErrored()
	{
		return errored;
	}
}
