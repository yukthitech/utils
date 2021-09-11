package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.common.AutomationUtils;

/**
 * All test case results of test suite.
 * 
 * @author akiran
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestSuiteResults
{
	/**
	 * Test suite name.
	 */
	private String suiteName;
	
	/**
	 * Author of the test suite.
	 */
	private String author;

	/**
	 * List of test case results.
	 */
	private List<TestCaseResult> testCaseResults = new ArrayList<>();
	
	/**
	 * Mapping from test case name to result.
	 */
	private Map<String, TestCaseResult> nameToResult = new HashMap<>();
	
	/**
	 * Status of the test suite.
	 */
	private TestStatus status;
	
	/**
	 * Flag indicates setup steps are executed successfully or not.
	 */
	private boolean setupSuccessful = false;
	
	/**
	 * Flag indicates cleanup steps are executed successfully or not.
	 */
	private boolean cleanupSuccessful = false;

	/**
	 * Status message.
	 */
	private String statusMessage;
	
	/**
	 * Start time of test suite.
	 */
	private Date startTime = new Date();
	
	/**
	 * End time of test suite.
	 */
	private Date endTime;

	/**
	 * Instantiates a new test suite results.
	 */
	public TestSuiteResults()
	{}

	/**
	 * Instantiates a new test suite results.
	 *
	 * @param testsuite the testsuite
	 */
	public TestSuiteResults(TestSuite testsuite)
	{
		this.suiteName = testsuite.getName();
		this.author = testsuite.getAuthor();
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	/**
	 * Sets the status of the test suite.
	 *
	 * @param status the new status of the test suite
	 */
	public void setStatus(TestStatus status)
	{
		this.status = status;
	}
	
	/**
	 * Gets the test suite status.
	 *
	 * @return the test suite status
	 */
	public TestStatus getStatus()
	{
		return status;
	}
	
	/**
	 * Gets the test suite status as string.
	 * @return test suite status as string.
	 */
	public String getStatusString()
	{
		return "" + status;
	}
	
	/**
	 * Sets the status message.
	 *
	 * @param statusMessage the new status message
	 */
	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}
	
	/**
	 * Gets the test suite status message.
	 *
	 * @return the test suite status message
	 */
	public String getStatusMessage()
	{
		return statusMessage;
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
	 * Gets the flag indicates setup steps are executed successfully or not.
	 *
	 * @return the flag indicates setup steps are executed successfully or not
	 */
	public boolean isSetupSuccessful()
	{
		return setupSuccessful;
	}

	/**
	 * Sets the flag indicates setup steps are executed successfully or not.
	 *
	 * @param setupSuccessful the new flag indicates setup steps are executed successfully or not
	 */
	public void setSetupSuccessful(boolean setupSuccessful)
	{
		this.setupSuccessful = setupSuccessful;
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
	 * Fetches number of test cases having specified status.
	 * @param status Status to be checked.
	 * @return Number of test cases with specified status.
	 */
	private int getStatusCount(TestStatus status)
	{
		int count = 0;
		
		for(TestCaseResult result : this.testCaseResults)
		{
			if(result.getStatus() == status)
			{
				count ++;
			}
		}
		
		return count;
	}

	/**
	 * Gets the test case success count in this suite.
	 *
	 * @return the test case success count in this suite
	 */
	public int getSuccessCount()
	{
		return getStatusCount(TestStatus.SUCCESSFUL);
	}

	/**
	 * Gets the test case failure count in this suite.
	 *
	 * @return the test case failure count in this suite
	 */
	public int getFailureCount()
	{
		return getStatusCount(TestStatus.FAILED);
	}

	/**
	 * Gets the test case error count in this suite.
	 *
	 * @return the test case error count in this suite
	 */
	public int getErrorCount()
	{
		return getStatusCount(TestStatus.ERRORED);
	}
	
	/**
	 * Gets the test case skip count in this suite.
	 *
	 * @return the test case skip count in this suite
	 */
	public int getSkipCount()
	{
		return getStatusCount(TestStatus.SKIPPED);
	}
	
	/**
	 * Fetches number of test cases.
	 * @return count
	 */
	public int getTotalCount()
	{
		return testCaseResults.size();
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

	/**
	 * Gets the flag indicates cleanup steps are executed successfully or not.
	 *
	 * @return the flag indicates cleanup steps are executed successfully or not
	 */
	public boolean isCleanupSuccessful()
	{
		return cleanupSuccessful;
	}

	/**
	 * Sets the flag indicates cleanup steps are executed successfully or not.
	 *
	 * @param cleanupSuccessful the new flag indicates cleanup steps are executed successfully or not
	 */
	public void setCleanupSuccessful(boolean cleanupSuccessful)
	{
		this.cleanupSuccessful = cleanupSuccessful;
	}

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}
	
	public String getTimeTaken()
	{
		return AutomationUtils.getTimeTaken(startTime, endTime);
	}
}
