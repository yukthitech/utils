package com.yukthitech.automation.logmon;

import com.yukthitech.automation.test.TestStatus;

/**
 * Context using which monitor log html will be generated.
 * @author akiran
 */
public class LogMonitorContext
{
	/**
	 * Test case name.
	 */
	private String testCaseName;
	
	/**
	 * Monitor name.
	 */
	private String monitorName;
	
	/**
	 * Content
	 */
	private String content;
	
	/**
	 * Status of the test case.
	 */
	private TestStatus status;
	
	/**
	 * Description of the test case.
	 */
	private String description;

	/**
	 * Instantiates a new log monitor context.
	 *
	 * @param testCaseName the test case name
	 * @param monitorName the monitor name
	 * @param content the content
	 * @param status the status
	 */
	public LogMonitorContext(String testCaseName, String monitorName, String content, TestStatus status, String description)
	{
		this.testCaseName = testCaseName;
		this.monitorName = monitorName;
		this.content = content;
		this.status = status;
		this.description = description;
	}

	/**
	 * Gets the test case name.
	 *
	 * @return the test case name
	 */
	public String getTestCaseName()
	{
		return testCaseName;
	}

	/**
	 * Gets the monitor name.
	 *
	 * @return the monitor name
	 */
	public String getMonitorName()
	{
		return monitorName;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}
	
	/**
	 * Gets the status of the test case.
	 *
	 * @return the status of the test case
	 */
	public TestStatus getStatus()
	{
		return status;
	}
	
	/**
	 * Gets the description of the test case.
	 *
	 * @return the description of the test case
	 */
	public String getDescription()
	{
		return description;
	}
}
