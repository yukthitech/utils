package com.yukthitech.automation.test;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yukthitech.automation.test.log.ExecutionLogData;

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
	private ExecutionLogData executionLog;
	
	/**
	 * Failure message.
	 */
	private String message;
	
	/**
	 * Monitor logs.
	 */
	private Map<String, String> monitorLogs = new HashMap<>();

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
	 * @param message the failure message
	 */
	public TestCaseResult(String testCaseName, TestStatus status, ExecutionLogData executionLog, String message)
	{
		this.testCaseName = testCaseName;
		this.status = status;
		this.executionLog = executionLog;
		this.message = message;
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
	@JsonIgnore
	public ExecutionLogData getExecutionLog()
	{
		return executionLog;
	}

	/**
	 * Sets the execution log.
	 *
	 * @param executionLog the new execution log
	 */
	public void setExecutionLog(ExecutionLogData executionLog)
	{
		this.executionLog = executionLog;
	}

	/**
	 * Gets the failure message.
	 *
	 * @return the failure message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the failure message.
	 *
	 * @param failureMessage the new failure message
	 */
	public void setMessage(String failureMessage)
	{
		this.message = failureMessage;
	}

	/**
	 * Gets the monitor logs.
	 *
	 * @return the monitor logs
	 */
	public Map<String, String> getMonitorLogs()
	{
		return monitorLogs;
	}
	
	/**
	 * Sets the monitor log.
	 *
	 * @param name the name
	 * @param fileName the file name
	 */
	public void setMonitorLog(String name, String fileName)
	{
		monitorLogs.put(name, fileName);
	}
}
