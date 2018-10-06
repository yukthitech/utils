package com.yukthitech.autox.ide.views.report;

import java.util.Date;

import com.yukthitech.autox.test.log.LogLevel;

public class LogReportRow
{
	private LogLevel logLevel;
	private String source;
	private String message;
	private Date time;

	public LogReportRow(LogLevel logLevel, String source, String message, Date time)
	{
		this.logLevel = logLevel;
		this.source = source;
		this.message = message;
		this.time = time;
	}

	public LogLevel getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel)
	{
		this.logLevel = logLevel;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}
}
