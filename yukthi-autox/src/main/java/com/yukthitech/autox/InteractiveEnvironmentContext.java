package com.yukthitech.autox;

import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseData;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.autox.test.TestSuiteGroup;

/**
 * Interactive environment context.
 * @author akiran
 */
public class InteractiveEnvironmentContext
{
	/**
	 * Test suite group loaded by launcher.
	 */
	private TestSuiteGroup testSuiteGroup;
	
	/**
	 * Execution controller to be used in this context.
	 */
	private InteractiveExecutionController executionController;
	
	/**
	 * Flag indicating if global setup has to be executed.
	 */
	private boolean executeGlobalSetup = true;
	
	/**
	 * Last test suite that got executed as part of this interactive context.
	 */
	private TestSuite lastTestSuite;
	
	/**
	 * Last test case that got executed as part of this interactive context.
	 */
	private TestCase lastTestCase;
	
	private TestCaseData lastTestCaseData;

	/**
	 * Instantiates a new interactive environment context.
	 *
	 * @param testSuiteGroup the test suite group
	 */
	public InteractiveEnvironmentContext(TestSuiteGroup testSuiteGroup)
	{
		this.testSuiteGroup = testSuiteGroup;
	}
	
	/**
	 * Gets the test suite group loaded by launcher.
	 *
	 * @return the test suite group loaded by launcher
	 */
	public TestSuiteGroup getTestSuiteGroup()
	{
		return testSuiteGroup;
	}

	/**
	 * Gets the execution controller to be used in this context.
	 *
	 * @return the execution controller to be used in this context
	 */
	public InteractiveExecutionController getExecutionController()
	{
		return executionController;
	}

	/**
	 * Sets the execution controller to be used in this context.
	 *
	 * @param executionController the new execution controller to be used in this context
	 */
	public void setExecutionController(InteractiveExecutionController executionController)
	{
		this.executionController = executionController;
	}

	/**
	 * Gets the flag indicating if global setup has to be executed.
	 *
	 * @return the flag indicating if global setup has to be executed
	 */
	public boolean isExecuteGlobalSetup()
	{
		return executeGlobalSetup;
	}

	/**
	 * Sets the flag indicating if global setup has to be executed.
	 *
	 * @param executeGlobalSetup the new flag indicating if global setup has to be executed
	 */
	public void setExecuteGlobalSetup(boolean executeGlobalSetup)
	{
		this.executeGlobalSetup = executeGlobalSetup;
	}

	/**
	 * Gets the last test suite that got executed as part of this interactive context.
	 *
	 * @return the last test suite that got executed as part of this interactive context
	 */
	public TestSuite getLastTestSuite()
	{
		return lastTestSuite;
	}

	/**
	 * Sets the last test suite that got executed as part of this interactive context.
	 *
	 * @param lastTestSuite the new last test suite that got executed as part of this interactive context
	 */
	public void setLastTestSuite(TestSuite lastTestSuite)
	{
		this.lastTestSuite = lastTestSuite;
	}

	/**
	 * Gets the last test case that got executed as part of this interactive context.
	 *
	 * @return the last test case that got executed as part of this interactive context
	 */
	public TestCase getLastTestCase()
	{
		return lastTestCase;
	}

	/**
	 * Sets the last test case.
	 *
	 * @param lastTestCase
	 *            the last test case
	 * @param testCaseData
	 *            the test case data
	 */
	public void setLastTestCase(TestCase lastTestCase, TestCaseData testCaseData)
	{
		this.lastTestCase = lastTestCase;
		this.lastTestCaseData = testCaseData;
	}
	
	/**
	 * Gets the last test case data.
	 *
	 * @return the last test case data
	 */
	public TestCaseData getLastTestCaseData()
	{
		return lastTestCaseData;
	}
}
