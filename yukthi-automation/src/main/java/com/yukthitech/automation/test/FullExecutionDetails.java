package com.yukthitech.automation.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Class FullExecutionDetails.
 */
public class FullExecutionDetails
{
	/**
	 * All test case results of test suite.
	 * 
	 * @author akiran
	 */
	public static class TestSuiteResults
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
		 * Test case success count in this suite.
		 */
		private int successCount = 0;

		/**
		 * Test case failure count in this suite.
		 */
		private int failureCount = 0;

		/**
		 * Test case error counr in this suite.
		 */
		private int errorCount = 0;

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
	}

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

		results.testCaseResults.add(result);

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
		else
		{
			this.errorCount++;
			results.errorCount++;
		}

		this.fullTestCount++;
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
}
