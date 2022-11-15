package com.yukthitech.test.beans;

import java.util.List;

import com.yukthitech.autox.exec.report.ExecutionLogData;

public class TestLogData
{
	private ExecutionLogData.Header header;
	
	private List<ExecutionLogData.Message> messages;
	
	private ExecutionLogData.Footer footer;

	public ExecutionLogData.Header getHeader()
	{
		return header;
	}

	public void setHeader(ExecutionLogData.Header header)
	{
		this.header = header;
	}

	public List<ExecutionLogData.Message> getMessages()
	{
		return messages;
	}

	public void setMessages(List<ExecutionLogData.Message> messages)
	{
		this.messages = messages;
	}

	public ExecutionLogData.Footer getFooter()
	{
		return footer;
	}

	public void setFooter(ExecutionLogData.Footer footer)
	{
		this.footer = footer;
	}
}
