package com.yukthitech.autox.event;

import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseResult;
import com.yukthitech.autox.test.TestSuite;

/**
 * Event object.
 * @author akiran
 */
public class AutomationEvent
{
	/**
	 * Test suite being executed.
	 */
	private TestSuite testSuite;
	
	/**
	 * test case being executed or completed.
	 */
	private TestCase testCase;
	
	/**
	 * In case of test case completion, the result.
	 */
	private TestCaseResult testCaseResult;

	/**
	 * Instantiates a new automation event.
	 *
	 * @param testSuite the test suite
	 * @param testCase the test case
	 * @param testCaseResult the test case result
	 */
	public AutomationEvent(TestSuite testSuite, TestCase testCase, TestCaseResult testCaseResult)
	{
		this.testSuite = testSuite;
		this.testCase = testCase;
		this.testCaseResult = testCaseResult;
	}

	/**
	 * Gets the test suite being executed.
	 *
	 * @return the test suite being executed
	 */
	public TestSuite getTestSuite()
	{
		return testSuite;
	}

	/**
	 * Gets the test case being executed or completed.
	 *
	 * @return the test case being executed or completed
	 */
	public TestCase getTestCase()
	{
		return testCase;
	}

	/**
	 * Gets the in case of test case completion, the result.
	 *
	 * @return the in case of test case completion, the result
	 */
	public TestCaseResult getTestCaseResult()
	{
		return testCaseResult;
	}
}
