package com.yukthitech.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Beand to parse final results json with limited read.
 * @author akiran
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionResult
{
	private int testCaseErroredCount;
	
	private int testCaseFailureCount;
	
	private int testCaseSkippedCount;

	public int getTestCaseErroredCount()
	{
		return testCaseErroredCount;
	}

	public void setTestCaseErroredCount(int testCaseErroredCount)
	{
		this.testCaseErroredCount = testCaseErroredCount;
	}

	public int getTestCaseFailureCount()
	{
		return testCaseFailureCount;
	}

	public void setTestCaseFailureCount(int testCaseFailureCount)
	{
		this.testCaseFailureCount = testCaseFailureCount;
	}

	public int getTestCaseSkippedCount()
	{
		return testCaseSkippedCount;
	}

	public void setTestCaseSkippedCount(int testCaseSkippedCount)
	{
		this.testCaseSkippedCount = testCaseSkippedCount;
	}
}
