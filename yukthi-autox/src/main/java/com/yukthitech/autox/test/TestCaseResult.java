package com.yukthitech.autox.test;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.test.log.ExecutionLogData;

/**
 * Represents result of a test case.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCaseResult
{
	/**
	 * Name of the test case.
	 */
	private String testCaseName;
	
	/**
	 * Author of test case.
	 */
	private String author;
	
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
	 * Name of the system log of the current test case.
	 */
	private String systemLogName;
	
	/**
	 * Flag indicating if this is accumulated result of data-provider results.
	 */
	private boolean accumlatedResult = false;

	public TestCaseResult(TestCase testCase, TestStatus status, ExecutionLogData executionLog, String message)
	{
		this(testCase, testCase.getName(), status, executionLog, message, false);
	}
	
	public TestCaseResult(TestCase testCase, String effectiveName, TestStatus status, ExecutionLogData executionLog, String message)
	{
		this(testCase, effectiveName, status, executionLog, message, false);
	}

	public TestCaseResult(TestCase testCase, TestStatus status, ExecutionLogData executionLog, String message, boolean accumlatedResult)
	{
		this(testCase, testCase.getName(), status, executionLog, message, accumlatedResult);
	}

	public TestCaseResult(TestCase testCase, String effectiveName, TestStatus status, ExecutionLogData executionLog, String message, boolean accumlatedResult)
	{
		this.testCaseName = effectiveName;
		this.author = testCase != null? testCase.getAuthor() : null;
		
		this.status = status;
		this.executionLog = executionLog;
		this.message = message;
		this.accumlatedResult = accumlatedResult;
	}
	
	public String getAuthor()
	{
		return author;
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

	/**
	 * Gets the name of the system log of the current test case.
	 *
	 * @return the name of the system log of the current test case
	 */
	public String getSystemLogName()
	{
		return systemLogName;
	}

	/**
	 * Sets the name of the system log of the current test case.
	 *
	 * @param systemLogName the new name of the system log of the current test case
	 */
	public void setSystemLogName(String systemLogName)
	{
		this.systemLogName = systemLogName;
	}
	
	@JsonIgnore
	public boolean isAccumlatedResult()
	{
		return accumlatedResult;
	}
}
