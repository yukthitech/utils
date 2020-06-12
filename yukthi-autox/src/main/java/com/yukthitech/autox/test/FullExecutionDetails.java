package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.client.utils.DateUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.config.ApplicationConfiguration;

/**
 * Final execution details used for report generation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
	 * List of summary messages.
	 */
	private List<String> summaryMessages = new ArrayList<String>();
	
	/**
	 * Keeps track of test cases which are in progress. This will help in avoiding 
	 * recursive inter dependent test cases.
	 */
	private Set<String> inprogressTestCases = new HashSet<>();

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
			results = new TestSuiteResults(suite);
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
	public void testSuiteInProgress(TestSuite testSuite)
	{
		this.inProgressTestSuites.add(testSuite.getName());
		suiteToResults.put(testSuite.getName(), new TestSuiteResults(testSuite));
	}
	
	/**
	 * Fetches test suite results with specified name.
	 * @param testSuite
	 * @return
	 */
	public TestSuiteResults getTestSuiteResults(String testSuite)
	{
		return suiteToResults.get(testSuite);
	}
	
	/**
	 * Sets the status and message for specified test suite.
	 * @param testSuite
	 * @param status
	 * @param statusMessage
	 */
	private TestSuiteResults updateTestSuiteStatus(TestSuite testSuite, TestStatus status, String statusMessage)
	{
		TestSuiteResults results = suiteToResults.get(testSuite.getName());

		if(results == null)
		{
			results = new TestSuiteResults(testSuite);
			suiteToResults.put(testSuite.getName(), results);
		}
		
		results.setStatus(status);
		results.setStatusMessage(statusMessage);
		return results;
	}
	
	/**
	 * Called when no testcase is executed as part of this test suite.
	 * @param testSuite
	 */
	public void removeTestSuite(TestSuite testSuite)
	{
		suiteToResults.remove(testSuite.getName());
	}
	
	/**
	 * Marks specified test suite as completed. 
	 * @param testSuite Test suite to be marked
	 */
	public TestSuiteResults testSuiteCompleted(TestSuite testSuite)
	{
		this.inProgressTestSuites.remove(testSuite.getName());
		return updateTestSuiteStatus(testSuite, TestStatus.SUCCESSFUL, "");
	}
	
	/**
	 * Marks specified test suite as failed. 
	 * @param testSuite Test suite to be marked
	 * @param statusMessage status message
	 */
	public TestSuiteResults testSuiteFailed(TestSuite testSuite, String statusMessage)
	{
		this.inProgressTestSuites.remove(testSuite.getName());
		return updateTestSuiteStatus(testSuite, TestStatus.FAILED, statusMessage);
	}
	
	/**
	 * Marks specified test suite as failed. 
	 * @param testSuite Test suite to be marked
	 * @param statusMessage status message
	 */
	public TestSuiteResults testSuiteSkipped(TestSuite testSuite, String statusMessage)
	{
		this.inProgressTestSuites.remove(testSuite.getName());
		return updateTestSuiteStatus(testSuite, TestStatus.SKIPPED, statusMessage);
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

	/**
	 * Gets the list of summary messages.
	 *
	 * @return the list of summary messages
	 */
	public List<String> getSummaryMessages()
	{
		return summaryMessages;
	}

	/**
	 * Sets the list of summary messages.
	 *
	 * @param summaryMessages the new list of summary messages
	 */
	public void setSummaryMessages(List<String> summaryMessages)
	{
		this.summaryMessages = summaryMessages;
	}
	
	/**
	 * Called when a test case execution/evaluation is started.
	 * @param testCase
	 */
	public void startedTestCase(String testCase)
	{
		this.inprogressTestCases.add(testCase);
	}
	
	/**
	 * Called when processing test case is completed.
	 * @param testCase
	 */
	public void closeTestCase(String testCase)
	{
		this.inprogressTestCases.remove(testCase);
	}
	
	/**
	 * Checks whether specified test case is in progress or not.
	 * @param testCase
	 * @return
	 */
	public boolean isTestCaseInProgress(String testCase)
	{
		return inprogressTestCases.contains(testCase);
	}
}
