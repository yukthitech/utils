package com.yukthitech.automation.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All test case results of test suite.
 * 
 * @author akiran
 */
public class TestSuiteResults
{
	/**
	 * Test suite name.
	 */
	private String suiteName;

	/**
	 * Test suite.
	 */
	private TestSuite testSuite;

	/**
	 * List of test case results.
	 */
	private List<TestCaseResult> testCaseResults = new ArrayList<>();
	
	/**
	 * Mapping from test case name to result.
	 */
	private Map<String, TestCaseResult> nameToResult = new HashMap<>();

	/**
	 * Test case success count in this suite.
	 */
	int successCount = 0;

	/**
	 * Test case failure count in this suite.
	 */
	int failureCount = 0;

	/**
	 * Test case error count in this suite.
	 */
	int errorCount = 0;
	
	/**
	 * Test case skip count in this suite.
	 */
	int skipCount = 0;

	/**
	 * Instantiates a new test suite results.
	 *
	 * @param suiteName the suite name
	 * @param suite the suite
	 */
	public TestSuiteResults(String suiteName, TestSuite suite)
	{
		this.suiteName = suiteName;
		this.testSuite = suite;
	}
	
	/**
	 * Gets the test suite status.
	 *
	 * @return the test suite status
	 */
	public TestSuiteStatus getStatus()
	{
		return testSuite.getStatus();
	}
	
	/**
	 * Gets the test suite status as string.
	 * @return test suite status as string.
	 */
	public String getStatusString()
	{
		return "" + testSuite.getStatus();
	}
	
	/**
	 * Gets the test suite status message.
	 *
	 * @return the test suite status message
	 */
	public String getStatusMessage()
	{
		return testSuite.getStatusMessage();
	}
	
	/**
	 * Gets the test suite name.
	 *
	 * @return the test suite name
	 */
	public String getSuiteName()
	{
		return suiteName;
	}

	/**
	 * Gets the list of test case results.
	 *
	 * @return the list of test case results
	 */
	public List<TestCaseResult> getTestCaseResults()
	{
		return testCaseResults;
	}

	/**
	 * Gets the test case success count in this suite.
	 *
	 * @return the test case success count in this suite
	 */
	public int getSuccessCount()
	{
		return successCount;
	}

	/**
	 * Gets the test case failure count in this suite.
	 *
	 * @return the test case failure count in this suite
	 */
	public int getFailureCount()
	{
		return failureCount;
	}

	/**
	 * Gets the test case error counr in this suite.
	 *
	 * @return the test case error counr in this suite
	 */
	public int getErrorCount()
	{
		return errorCount;
	}
	
	/**
	 * Gets the test case skip count in this suite.
	 *
	 * @return the test case skip count in this suite
	 */
	public int getSkipCount()
	{
		return skipCount;
	}
	
	/**
	 * Adds specified test result to this suite.
	 * @param result result to add
	 */
	public void addResult(TestCaseResult result)
	{
		this.testCaseResults.add(result);
		this.nameToResult.put(result.getTestCaseName(), result);
	}
	
	/**
	 * Fetches result of specified test case.
	 * @param testCaseName test case result to fetch
	 * @return result
	 */
	public TestCaseResult getTestCaseResult(String testCaseName)
	{
		return this.nameToResult.get(testCaseName);
	}
}
