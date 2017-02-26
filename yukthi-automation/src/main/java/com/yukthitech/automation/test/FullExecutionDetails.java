package com.yukthitech.automation.test;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Class FullExecutionDetails.
 */
public class FullExecutionDetails
{
	/**
	 * The suite to results.
	 */
	private Map<String, TestSuiteResults> suiteToResults = new TreeMap<>();

	/**
	 * Full test count.
	 */
	private int fullTestCount = 0;

	/**
	 * Full success count.
	 */
	private int successCount = 0;

	/**
	 * Full failure count.
	 */
	private int failureCount = 0;

	/**
	 * Full error count.
	 */
	private int errorCount = 0;

	/**
	 * Full skip count.
	 */
	private int skipCount = 0;

	/**
	 * Adds specified test result in specified suite.
	 * 
	 * @param suite
	 *            test suite.
	 * @param result
	 *            Test result.
	 */
	public void addTestResult(TestSuite suite, TestCaseResult result)
	{
		TestSuiteResults results = suiteToResults.get(suite.getName());

		if(results == null)
		{
			results = new TestSuiteResults(suite.getName(), suite);
			suiteToResults.put(suite.getName(), results);
		}

		results.addResult(result);

		if(result.getStatus() == TestStatus.SUCCESSUFUL)
		{
			this.successCount++;
			results.successCount++;
		}
		else if(result.getStatus() == TestStatus.FAILED)
		{
			this.failureCount++;
			results.failureCount++;
		}
		else if(result.getStatus() == TestStatus.SKIPPED)
		{
			this.skipCount++;
			results.skipCount++;
		}
		else
		{
			this.errorCount++;
			results.errorCount++;
		}

		this.fullTestCount++;
	}
	
	/**
	 * Fetches test case result of specified test case.
	 * @param testSuiteName Parent test suite name
	 * @param testCaseName test case name
	 * @return test case result, null if it does not exist yet
	 */
	public TestCaseResult getTestCaseResult(String testSuiteName, String testCaseName)
	{
		TestSuiteResults suiteResults = this.suiteToResults.get(testSuiteName);
		
		if(suiteResults == null)
		{
			return null;
		}
		
		return suiteResults.getTestCaseResult(testCaseName);
	}

	/**
	 * Gets the suite to results.
	 *
	 * @return the suite to results
	 */
	public Collection<TestSuiteResults> getTestSuiteResults()
	{
		return suiteToResults.values();
	}

	/**
	 * Gets the full test count.
	 *
	 * @return the full test count
	 */
	public int getFullTestCount()
	{
		return fullTestCount;
	}

	/**
	 * Gets the full success count.
	 *
	 * @return the full success count
	 */
	public int getSuccessCount()
	{
		return successCount;
	}

	/**
	 * Gets the full failure count.
	 *
	 * @return the full failure count
	 */
	public int getFailureCount()
	{
		return failureCount;
	}

	/**
	 * Gets the full error count.
	 *
	 * @return the full error count
	 */
	public int getErrorCount()
	{
		return errorCount;
	}
	
	/**
	 * Gets the full skip count.
	 *
	 * @return the full skip count
	 */
	public int getSkipCount()
	{
		return skipCount;
	}
}
