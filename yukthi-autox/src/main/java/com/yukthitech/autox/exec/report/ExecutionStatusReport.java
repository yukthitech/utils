package com.yukthitech.autox.exec.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.test.TestStatus;

/**
 * Represents an execution status report.
 * @author akranthikiran
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionStatusReport
{
	/**
	 * Name of execution.
	 */
	private String name;
	
	/**
	 * Author of the execution.
	 */
	private String author;

	/**
	 * List of child reports.
	 */
	private List<ExecutionStatusReport> childReports;
	
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public List<ExecutionStatusReport> getChildReports()
	{
		return childReports;
	}

	public void setChildReports(List<ExecutionStatusReport> childReports)
	{
		this.childReports = childReports;
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

	public void addChidReport(ExecutionStatusReport report)
	{
		if(childReports == null)
		{
			childReports = new ArrayList<>();
		}
		
		childReports.add(report);
	}
	
	/**
	 * Fetches number of child reports having specified status.
	 * @param status Status to be checked.
	 * @return Number of child reports with specified status.
	 */
	private int getStatusCount(TestStatus status)
	{
		if(CollectionUtils.isEmpty(childReports))
		{
			return 0;
		}
		
		int count = 0;
		
		for(ExecutionStatusReport result : this.childReports)
		{
			if(result.getMainExecutionDetails() != null && result.getMainExecutionDetails().getStatus() == status)
			{
				count ++;
			}
		}
		
		return count;
	}

	/**
	 * Gets the child reports success count in this suite.
	 *
	 * @return the child reports success count in this suite
	 */
	public int getSuccessCount()
	{
		return getStatusCount(TestStatus.SUCCESSFUL);
	}

	/**
	 * Gets the child reports failure count in this suite.
	 *
	 * @return the child reports failure count in this suite
	 */
	public int getFailureCount()
	{
		return getStatusCount(TestStatus.FAILED);
	}

	/**
	 * Gets the child reports error count in this suite.
	 *
	 * @return the child reports error count in this suite
	 */
	public int getErrorCount()
	{
		return getStatusCount(TestStatus.ERRORED);
	}
	
	/**
	 * Gets the child reports skip count in this suite.
	 *
	 * @return the child reports skip count in this suite
	 */
	public int getSkipCount()
	{
		return getStatusCount(TestStatus.SKIPPED);
	}
	
	/**
	 * Fetches number of child reports.
	 * @return count
	 */
	public int getTotalCount()
	{
		if(childReports == null)
		{
			return 0;
		}
		
		return childReports.size();
	}

}