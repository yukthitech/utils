package com.yukthitech.autox;

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
}
