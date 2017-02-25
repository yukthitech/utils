package com.yukthitech.automation.test;

/**
 * The Class TestCaseResult.
 */
public class TestCaseResult
{
	/**
	 * Name of the test case.
	 */
	private String testCaseName;
	
	/**
	 * Status of the test case execution.
	 */
	private TestStatus status;
	
	/**
	 *  execution log.
	 */
	private String executionLog;
	
	/**
	 * Failure message.
	 */
	private String failureMessage;

	/**
	 * Instantiates a new test case result.
	 */
	public TestCaseResult()
	{}
	
	/**
	 * Instantiates a new test case result.
	 *
	 * @param testCaseName Test case name
	 * @param status the status
	 * @param executionLog the execution log
	 * @param failureMessage the failure message
	 */
	public TestCaseResult(String testCaseName, TestStatus status, String executionLog, String failureMessage)
	{
		this.testCaseName = testCaseName;
		this.status = status;
		this.executionLog = executionLog;
		this.failureMessage = failureMessage;
	}
	
	/**
	 * Gets the name of the test case.
	 *
	 * @return the name of the test case
	 */
	public String getTestCaseName()
	{
		return testCaseName;
	}

	/**
	 * Sets the name of the test case.
	 *
	 * @param testCaseName the new name of the test case
	 */
	public void setTestCaseName(String testCaseName)
	{
		this.testCaseName = testCaseName;
	}

	/**
	 * Gets the status of the test case execution.
	 *
	 * @return the status of the test case execution
	 */
	public TestStatus getStatus()
	{
		return status;
	}

	/**
	 * Sets the status of the test case execution.
	 *
	 * @param status the new status of the test case execution
	 */
	public void setStatus(TestStatus status)
	{
		this.status = status;
	}
	
	/**
	 * Fetches status as string.
	 * @return Status name.
	 */
	public String getStatusString()
	{
		return "" + status;
	}

	/**
	 * Gets the execution log.
	 *
	 * @return the execution log
	 */
	public String getExecutionLog()
	{
		return executionLog;
	}

	/**
	 * Sets the execution log.
	 *
	 * @param executionLog the new execution log
	 */
	public void setExecutionLog(String executionLog)
	{
		this.executionLog = executionLog;
	}

	/**
	 * Gets the failure message.
	 *
	 * @return the failure message
	 */
	public String getFailureMessage()
	{
		return failureMessage;
	}

	/**
	 * Sets the failure message.
	 *
	 * @param failureMessage the new failure message
	 */
	public void setFailureMessage(String failureMessage)
	{
		this.failureMessage = failureMessage;
	}
}
