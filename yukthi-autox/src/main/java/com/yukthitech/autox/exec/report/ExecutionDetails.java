package com.yukthitech.autox.exec.report;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.test.TestStatus;

/**
 * Represents duration of execution.
 * @author akranthikiran
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionDetails
{
	/**
	 * Start time.
	 */
	private Date startTime = new Date();
	
	/**
	 * End time.
	 */
	private Date endTime;
	
	/**
	 * Status of execution.
	 */
	private TestStatus status = TestStatus.IN_PROGRESS;
	
	/**
	 * Status message.
	 */
	private String statusMessage;
	
	public Date setEndDetails(TestStatus status, String mssg)
	{
		this.endTime = new Date();
		this.status = status;
		this.statusMessage = mssg;
		
		return endTime;
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

	public TestStatus getStatus()
	{
		return status;
	}

	public void setStatus(TestStatus status)
	{
		this.status = status;
	}

	public String getTimeTaken()
	{
		return AutomationUtils.getTimeTaken(startTime, endTime);
	}

	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}
	
	public String getStartTimeStr()
	{
		return ApplicationConfiguration.getInstance().getTimeFormatObject().format(startTime);
	}

	public String getEndTimeStr()
	{
		return ApplicationConfiguration.getInstance().getTimeFormatObject().format(endTime);
	}
}
