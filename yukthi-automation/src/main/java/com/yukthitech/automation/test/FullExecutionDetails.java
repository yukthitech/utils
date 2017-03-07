package com.yukthitech.automation.test;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.client.utils.DateUtils;

import com.yukthitech.automation.config.ApplicationConfiguration;

/**
 * Final execution details used for report generation.
 */
public class FullExecutionDetails
{
	/**
	 * Name of the report.
	 */
	private String reportName;
	
	/**
	 * Date on which execution was started.
	 */
	private Date executionDate = new Date();
	
	/**
	 * Flag indicating if global setup was successful.
	 */
	private boolean setupSuccessful = true;
	
	/**
	 * Flag indicating if global cleanup was successful.
	 */
	private boolean cleanupSuccessful = true;
	
	/**
	 * The suite to results.
	 */
	private Map<String, TestSuiteResults> suiteToResults = new TreeMap<>();

	/**
	 * Maintains test suites in progress.
	 */
	private Set<String> inProgressTestSuites = new HashSet<>();
	

	/**
	 * Gets the name of the report.
	 *
	 * @return the name of the report
	 */
	public String getReportName()
	{
		return reportName;
	}

	/**
	 * Sets the name of the report.
	 *
	 * @param reportName the new name of the report
	 */
	public void setReportName(String reportName)
	{
		this.reportName = reportName;
	}

	/**
	 * Gets the date on which execution was started.
	 *
	 * @return the date on which execution was started
	 */
	public Date getExecutionDate()
	{
		return executionDate;
	}

	/**
	 * Gets the date on which execution was started.
	 *
	 * @return the date on which execution was started
	 */
	public String getExecutionDateStr()
	{
		return DateUtils.formatDate(executionDate, ApplicationConfiguration.getInstance().getDateFomat());
	}

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
			results = new TestSuiteResults(suite.getName());
			suiteToResults.put(suite.getName(), results);
		}

		results.addResult(result);
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
	 * Fetches number of test suites having specified status.
	 * @param status Status to be checked.
	 * @return Number of test suites with specified status.
	 */
	private int getTestSuiteCount(TestStatus status)
	{
		int count = 0;
		
		for(TestSuiteResults result : this.suiteToResults.values())
		{
			if(result.getStatus() == status)
			{
				count ++;
			}
		}
		
		return count;
	}

	/**
	 * Fetches number of test cases having specified status.
	 * @param status Status to be checked.
	 * @return Number of test cases with specified status.
	 */
	private int getTestCaseCount(TestStatus status)
	{
		int count = 0;
		
		for(TestSuiteResults result : this.suiteToResults.values())
		{
			if(status == null)
			{
				count += result.getTotalCount();
				continue;
			}
			
			switch (status)
			{
				case SUCCESSFUL:
					count += result.getSuccessCount();
				break;
				case ERRORED:
					count += result.getErrorCount();
				break;
				case FAILED:
					count += result.getFailureCount();
				break;
				case SKIPPED:
					count += result.getSkipCount();
				break;
			}
		}
		
		return count;
	}
	
	/**
	 * Fetches number of test suites.
	 * @return number of test suites
	 */
	public int getTestSuiteCount()
	{
		return this.suiteToResults.size();
	}

	/**
	 * Fetches number of success test suites.
	 * @return number of success test suites
	 */
	public int getTestSuiteSuccessCount()
	{
		return getTestSuiteCount(TestStatus.SUCCESSFUL);
	}

	/**
	 * Fetches number of failure test suites.
	 * @return number of failure test suites
	 */
	public int getTestSuiteFailureCount()
	{
		return getTestSuiteCount(TestStatus.FAILED);
	}

	/**
	 * Fetches number of skipped test suites.
	 * @return number of skipped test suites
	 */
	public int getTestSuiteSkippedCount()
	{
		return getTestSuiteCount(TestStatus.SKIPPED);
	}

	/**
	 * Fetches number of test cases.
	 * @return number of test cases
	 */
	public int getTestCaseCount()
	{
		return getTestCaseCount(null);
	}

	/**
	 * Fetches number of success test cases.
	 * @return number of success test cases
	 */
	public int getTestCaseSuccessCount()
	{
		return getTestCaseCount(TestStatus.SUCCESSFUL);
	}

	/**
	 * Fetches number of failure test cases.
	 * @return number of failure test cases
	 */
	public int getTestCaseFailureCount()
	{
		return getTestCaseCount(TestStatus.FAILED);
	}

	/**
	 * Fetches number of errored test cases.
	 * @return number of errored test cases
	 */
	public int getTestCaseErroredCount()
	{
		return getTestCaseCount(TestStatus.ERRORED);
	}

	/**
	 * Fetches number of skipped test cases.
	 * @return number of skipped test cases
	 */
	public int getTestCaseSkippedCount()
	{
		return getTestCaseCount(TestStatus.SKIPPED);
	}

	/**
	 * Checks if is flag indicating if global setup was successful.
	 *
	 * @return the flag indicating if global setup was successful
	 */
	public boolean isSetupSuccessful()
	{
		return setupSuccessful;
	}

	/**
	 * Sets the flag indicating if global setup was successful.
	 *
	 * @param setupSuccessful the new flag indicating if global setup was successful
	 */
	public void setSetupSuccessful(boolean setupSuccessful)
	{
		this.setupSuccessful = setupSuccessful;
	}

	/**
	 * Checks if is flag indicating if global cleanup was successful.
	 *
	 * @return the flag indicating if global cleanup was successful
	 */
	public boolean isCleanupSuccessful()
	{
		return cleanupSuccessful;
	}

	/**
	 * Sets the flag indicating if global cleanup was successful.
	 *
	 * @param cleanupSuccessful the new flag indicating if global cleanup was successful
	 */
	public void setCleanupSuccessful(boolean cleanupSuccessful)
	{
		this.cleanupSuccessful = cleanupSuccessful;
	}

	/**
	 * Marks specified test suite as in progress. 
	 * @param testSuite Test suite to be marked
	 */
	public void testSuiteInProgress(String testSuite)
	{
		this.inProgressTestSuites.add(testSuite);
	}
	
	/**
	 * Sets the status and message for specified test suite.
	 * @param testSuite
	 * @param status
	 * @param statusMessage
	 */
	private void updateTestSuiteStatus(TestSuite testSuite, TestStatus status, String statusMessage)
	{
		TestSuiteResults results = suiteToResults.get(testSuite.getName());

		if(results == null)
		{
			results = new TestSuiteResults(testSuite.getName());
			suiteToResults.put(testSuite.getName(), results);
		}
		
		results.setStatus(status);
	}
	
	
	/**
	 * Marks specified test suite as completed. 
	 * @param testSuite Test suite to be marked
	 */
	public void testSuiteCompleted(TestSuite testSuite)
	{
		this.inProgressTestSuites.remove(testSuite);
		updateTestSuiteStatus(testSuite, TestStatus.SUCCESSFUL, "");
	}
	
	/**
	 * Marks specified test suite as failed. 
	 * @param testSuite Test suite to be marked
	 * @param statusMessage status message
	 */
	public void testSuiteFailed(TestSuite testSuite, String statusMessage)
	{
		this.inProgressTestSuites.remove(testSuite);
		updateTestSuiteStatus(testSuite, TestStatus.FAILED, statusMessage);
	}
	
	/**
	 * Marks specified test suite as failed. 
	 * @param testSuite Test suite to be marked
	 * @param statusMessage status message
	 */
	public void testSuiteSkipped(TestSuite testSuite, String statusMessage)
	{
		this.inProgressTestSuites.remove(testSuite);
		updateTestSuiteStatus(testSuite, TestStatus.SKIPPED, statusMessage);
	}
	
	/**
	 * Checks if the specified test suite is completed.
	 * @param testSuite Test suite to check
	 * @return true if completed
	 */
	public boolean isTestSuiteCompleted(String testSuite)
	{
		TestSuiteResults results = suiteToResults.get(testSuite);
		
		if(results == null)
		{
			return false;
		}
		
		return results.getStatus() == TestStatus.SUCCESSFUL;
	}
	
	/**
	 * Checks if the specified test suite is failed.
	 * @param testSuite Test suite to check
	 * @return true if failed
	 */
	public boolean isTestSuiteFailed(String testSuite)
	{
		TestSuiteResults results = suiteToResults.get(testSuite);
		
		if(results == null)
		{
			return false;
		}
		
		return results.getStatus() != TestStatus.SUCCESSFUL;
	}
	
	/**
	 * Checks if the specified test suite is completed or failed.
	 * @param testSuite Test suite to check
	 * @return true if completed or failed
	 */
	public boolean isTestSuiteExecuted(String testSuite)
	{
		TestSuiteResults results = suiteToResults.get(testSuite);
		
		if(results == null)
		{
			return false;
		}
		
		return results.getStatus() != null;
	}
	
	/**
	 * Checks if the specified test suite is in-progress.
	 * @param testSuite Test suite to check
	 * @return true if in-progress
	 */
	public boolean isTestSuiteInProgress(String testSuite)
	{
		return inProgressTestSuites.contains(testSuite);
	}
	
}
