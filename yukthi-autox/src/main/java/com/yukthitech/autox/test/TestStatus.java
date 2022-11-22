package com.yukthitech.autox.test;

/**
 * Test case execution status.
 * @author akiran
 */
public enum TestStatus
{
	/**
	 * Indicates execution is in progress.
	 */
	IN_PROGRESS(false),
	
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
	
	public static TestStatus getEffectiveStatus(TestStatus status1, TestStatus status2)
	{
		if(status1 == TestStatus.ERRORED || status2 == TestStatus.ERRORED)
		{
			return ERRORED;
		}
		
		if(status1 == TestStatus.FAILED || status2 == TestStatus.FAILED)
		{
			return FAILED;
		}

		if(status1 == TestStatus.SKIPPED || status2 == TestStatus.SKIPPED)
		{
			return SKIPPED;
		}

		if(status1 == TestStatus.IN_PROGRESS || status2 == TestStatus.IN_PROGRESS)
		{
			return IN_PROGRESS;
		}
		
		return SUCCESSFUL;
	}
}
