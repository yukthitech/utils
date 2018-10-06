package com.yukthitech.autox.monitor;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.yukthitech.autox.test.log.ExecutionLogData;
import com.yukthitech.autox.test.log.ExecutionLogData.Message;

/**
 * Log message to be sent monitoring client.
 * @author akiran
 */
public class MonitorLogMessage implements Serializable
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The test suite.
	 */
	private String testSuite;
	
	/**
	 * The test case.
	 */
	private String testCase;
	
	/**
	 * The test data name.
	 */
	private String testDataName;
	
	/**
	 * The message.
	 */
	private ExecutionLogData.Message message;
	
	/**
	 * Flag indicating if this the log message is part of setup.
	 */
	private boolean setup;
	
	/**
	 * Flag indicating if this the log message is part of cleanup.
	 */
	private boolean cleanup;

	/**
	 * Instantiates a new monitor log message.
	 *
	 * @param testSuite the test suite
	 * @param testCase the test case
	 * @param testDataName the test data name
	 * @param message the message
	 * @param setup the setup
	 * @param cleanup the cleanup
	 */
	public MonitorLogMessage(String testSuite, String testCase, String testDataName, Message message, boolean setup, boolean cleanup)
	{
		this.testSuite = testSuite;
		this.testCase = testCase;
		this.testDataName = testDataName;
		this.message = message;
		this.setup = setup;
		this.cleanup = cleanup;
	}

	/**
	 * Gets the test suite.
	 *
	 * @return the test suite
	 */
	public String getTestSuite()
	{
		return testSuite;
	}

	/**
	 * Gets the test case.
	 *
	 * @return the test case
	 */
	public String getTestCase()
	{
		return testCase;
	}

	/**
	 * Gets the test data name.
	 *
	 * @return the test data name
	 */
	public String getTestDataName()
	{
		return testDataName;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public ExecutionLogData.Message getMessage()
	{
		return message;
	}

	/**
	 * Gets the flag indicating if this the log message is part of setup.
	 *
	 * @return the flag indicating if this the log message is part of setup
	 */
	public boolean isSetup()
	{
		return setup;
	}

	/**
	 * Gets the flag indicating if this the log message is part of cleanup.
	 *
	 * @return the flag indicating if this the log message is part of cleanup
	 */
	public boolean isCleanup()
	{
		return cleanup;
	}
	
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
