package com.yukthitech.autox.exec.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.config.ApplicationConfiguration;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FinalReport
{
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TestSuiteResult
	{
		private ExecutionStatusReport report;
		
		private int totalCount;
		
		private int successCount;
		
		private int failureCount;
		
		private int errorCount;
		
		private int skipCount;
		
		private List<ExecutionStatusReport> testCaseResults = new ArrayList<>();
		
		public TestSuiteResult()
		{}
		
		public TestSuiteResult(ExecutionStatusReport report)
		{
			this.report = report;
			addTestCases(report);
		}
		
		private void addTestCases(ExecutionStatusReport parent)
		{
			if(CollectionUtils.isEmpty(parent.getChildReports()))
			{
				return;
			}
			
			for(ExecutionStatusReport report : parent.getChildReports())
			{
				if(CollectionUtils.isNotEmpty(report.getChildReports()))
				{
					addTestCases(report);
					continue;
				}
				
				switch(report.getMainExecutionDetails().getStatus())
				{
					case ERRORED:
						errorCount++;
						break;
					case FAILED:
						failureCount++;
						break;
					case SKIPPED:
						skipCount++;
						break;
					default:
						successCount++;
				}
				
				testCaseResults.add(report);
				totalCount++;
			}
		}

		public ExecutionStatusReport getReport()
		{
			return report;
		}

		public void setReport(ExecutionStatusReport report)
		{
			this.report = report;
		}

		public int getTotalCount()
		{
			return totalCount;
		}

		public void setTotalCount(int totalCount)
		{
			this.totalCount = totalCount;
		}

		public int getSuccessCount()
		{
			return successCount;
		}

		public void setSuccessCount(int successCount)
		{
			this.successCount = successCount;
		}

		public int getFailureCount()
		{
			return failureCount;
		}

		public void setFailureCount(int failureCount)
		{
			this.failureCount = failureCount;
		}

		public int getErrorCount()
		{
			return errorCount;
		}

		public void setErrorCount(int errorCount)
		{
			this.errorCount = errorCount;
		}

		public int getSkipCount()
		{
			return skipCount;
		}

		public void setSkipCount(int skipCount)
		{
			this.skipCount = skipCount;
		}

		public List<ExecutionStatusReport> getTestCaseResults()
		{
			return testCaseResults;
		}
		
		public ExecutionStatusReport getTestCaseResult(String name)
		{
			return testCaseResults
					.stream()
					.filter(res -> name.equals(res.getName()))
					.findFirst()
					.orElse(null);
		}

		public void setTestCaseResults(List<ExecutionStatusReport> testCaseResults)
		{
			this.testCaseResults = testCaseResults;
		}
	}
	
	private String reportName;
	
	private Date executionDate = new Date();
	
	private int testSuiteCount;
	
	private int testSuiteSuccessCount;
	
	private int testSuiteErrorCount;
	
	private int testSuiteFailureCount;
	
	private int testSuiteSkippedCount;
	
	private int testCaseCount;
	
	private int testCaseSuccessCount;
	
	private int testCaseFailureCount;
	
	private int testCaseErroredCount;
	
	private int testCaseSkippedCount;
	
	private List<TestSuiteResult> testSuiteResults = new ArrayList<>();
	
	/**
	 * Setup execution details.
	 */
	private ExecutionDetails setupExecutionDetails;

	/**
	 * Main execution details.
	 */
	private ExecutionDetails mainExecutionDetails;

	/**
	 * Cleanup execution details.
	 */
	private ExecutionDetails cleanupExecutionDetails;
	
	public FinalReport()
	{}
	
	public FinalReport(String reportName, ExecutionStatusReport testSuiteGroupReport)
	{
		this.reportName = reportName;
		this.setupExecutionDetails = testSuiteGroupReport.getSetupExecutionDetails();
		this.mainExecutionDetails = testSuiteGroupReport.getMainExecutionDetails();
		this.cleanupExecutionDetails = testSuiteGroupReport.getCleanupExecutionDetails();
		
		if(testSuiteGroupReport.getChildReports() == null)
		{
			return;
		}
		
		for(ExecutionStatusReport testSuiteReport : testSuiteGroupReport.getChildReports())
		{
			TestSuiteResult testSuiteResult = new TestSuiteResult(testSuiteReport);
			
			switch (testSuiteReport.getMainExecutionDetails().getStatus())
			{
				case ERRORED:
					testSuiteErrorCount++;
					break;
				case FAILED:
					testSuiteFailureCount++;
					break;
				case SKIPPED:
					testSuiteSkippedCount++;
					break;
				default:
					testSuiteSuccessCount++;
			}
			
			testSuiteCount++;
			
			testCaseCount += testSuiteResult.totalCount;
			testCaseSuccessCount += testSuiteResult.successCount;
			testCaseFailureCount += testSuiteResult.failureCount;
			testCaseErroredCount += testSuiteResult.errorCount;
			testCaseSkippedCount += testSuiteResult.skipCount;
			
			testSuiteResults.add(testSuiteResult);
		}
	}

	public String getReportName()
	{
		return reportName;
	}

	public void setReportName(String reportName)
	{
		this.reportName = reportName;
	}
	
	public String getExecutionDateStr()
	{
		return ApplicationConfiguration.getInstance().getTimeFormatObject().format(executionDate);
	}

	public Date getExecutionDate()
	{
		return executionDate;
	}

	public void setExecutionDate(Date executionDate)
	{
		this.executionDate = executionDate;
	}

	public int getTestSuiteCount()
	{
		return testSuiteCount;
	}

	public void setTestSuiteCount(int testSuiteCount)
	{
		this.testSuiteCount = testSuiteCount;
	}

	public int getTestSuiteSuccessCount()
	{
		return testSuiteSuccessCount;
	}

	public void setTestSuiteSuccessCount(int testSuiteSuccessCount)
	{
		this.testSuiteSuccessCount = testSuiteSuccessCount;
	}

	public int getTestSuiteErrorCount()
	{
		return testSuiteErrorCount;
	}

	public void setTestSuiteErrorCount(int testSuiteErrorCount)
	{
		this.testSuiteErrorCount = testSuiteErrorCount;
	}

	public int getTestSuiteFailureCount()
	{
		return testSuiteFailureCount;
	}

	public void setTestSuiteFailureCount(int testSuiteFailureCount)
	{
		this.testSuiteFailureCount = testSuiteFailureCount;
	}

	public int getTestSuiteSkippedCount()
	{
		return testSuiteSkippedCount;
	}

	public void setTestSuiteSkippedCount(int testSuiteSkippedCount)
	{
		this.testSuiteSkippedCount = testSuiteSkippedCount;
	}

	public int getTestCaseCount()
	{
		return testCaseCount;
	}

	public void setTestCaseCount(int testCaseCount)
	{
		this.testCaseCount = testCaseCount;
	}

	public int getTestCaseSuccessCount()
	{
		return testCaseSuccessCount;
	}

	public void setTestCaseSuccessCount(int testCaseSuccessCount)
	{
		this.testCaseSuccessCount = testCaseSuccessCount;
	}

	public int getTestCaseFailureCount()
	{
		return testCaseFailureCount;
	}

	public void setTestCaseFailureCount(int testCaseFailureCount)
	{
		this.testCaseFailureCount = testCaseFailureCount;
	}

	public int getTestCaseErroredCount()
	{
		return testCaseErroredCount;
	}

	public void setTestCaseErroredCount(int testCaseErroredCount)
	{
		this.testCaseErroredCount = testCaseErroredCount;
	}

	public int getTestCaseSkippedCount()
	{
		return testCaseSkippedCount;
	}

	public void setTestCaseSkippedCount(int testCaseSkippedCount)
	{
		this.testCaseSkippedCount = testCaseSkippedCount;
	}

	public List<TestSuiteResult> getTestSuiteResults()
	{
		return testSuiteResults;
	}
	
	public TestSuiteResult getTestSuiteResult(String name)
	{
		return this.testSuiteResults
				.stream()
				.filter(res -> res.getReport().getName().equals(name))
				.findFirst()
				.orElse(null);
	}

	public void setTestSuiteResults(List<TestSuiteResult> testSuiteResults)
	{
		this.testSuiteResults = testSuiteResults;
	}

	public ExecutionDetails getSetupExecutionDetails()
	{
		return setupExecutionDetails;
	}

	public void setSetupExecutionDetails(ExecutionDetails setupExecutionDetails)
	{
		this.setupExecutionDetails = setupExecutionDetails;
	}

	public ExecutionDetails getMainExecutionDetails()
	{
		return mainExecutionDetails;
	}

	public void setMainExecutionDetails(ExecutionDetails mainExecutionDetails)
	{
		this.mainExecutionDetails = mainExecutionDetails;
	}

	public ExecutionDetails getCleanupExecutionDetails()
	{
		return cleanupExecutionDetails;
	}

	public void setCleanupExecutionDetails(ExecutionDetails cleanupExecutionDetails)
	{
		this.cleanupExecutionDetails = cleanupExecutionDetails;
	}
}