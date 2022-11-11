package com.yukthitech.autox.exec.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.common.AutomationUtils;
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
	 * Status of the execution.
	 * Null indicates execution is yet to be started.
	 */
	private TestStatus status;
	
	/**
	 * Flag indicates setup steps are executed successfully or not.
	 * Null indicates there was no setup.
	 */
	private Boolean setupSuccessful;
	
	/**
	 * Flag indicates cleanup steps are executed successfully or not.
	 * Null indicates there was no cleanup.
	 */
	private Boolean cleanupSuccessful;

	/**
	 * Status message.
	 */
	private String statusMessage;
	
	/**
	 * Start time.
	 */
	private Date startTime = new Date();
	
	/**
	 * End time.
	 */
	private Date endTime;

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

	public TestStatus getStatus()
	{
		return status;
	}

	public void setStatus(TestStatus status)
	{
		this.status = status;
	}

	public Boolean getSetupSuccessful()
	{
		return setupSuccessful;
	}

	public void setSetupSuccessful(Boolean setupSuccessful)
	{
		this.setupSuccessful = setupSuccessful;
	}

	public Boolean getCleanupSuccessful()
	{
		return cleanupSuccessful;
	}

	public void setCleanupSuccessful(Boolean cleanupSuccessful)
	{
		this.cleanupSuccessful = cleanupSuccessful;
	}

	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
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
			if(result.getStatus() == status)
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
		return childReports.size();
	}

}
