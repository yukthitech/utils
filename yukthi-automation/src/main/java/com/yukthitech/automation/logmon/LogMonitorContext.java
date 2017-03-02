package com.yukthitech.automation.logmon;

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
	 * Content lines
	 */
	private String contentLines[];

	/**
	 * Instantiates a new log monitor context.
	 *
	 * @param testCaseName the test case name
	 * @param monitorName the monitor name
	 * @param content the content
	 */
	public LogMonitorContext(String testCaseName, String monitorName, String content)
	{
		this.testCaseName = testCaseName;
		this.monitorName = monitorName;
		
		if(content != null)
		{
			this.contentLines = content.split("\\n");
		}
		else
		{
			this.contentLines = new String[]{""};
		}
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
	 * Gets the content lines.
	 *
	 * @return the content lines
	 */
	public String[] getContentLines()
	{
		return contentLines;
	}
}
