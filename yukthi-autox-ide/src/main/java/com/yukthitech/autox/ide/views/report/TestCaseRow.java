package com.yukthitech.autox.ide.views.report;

import com.yukthitech.autox.test.log.ExecutionLogData;

public class TestCaseRow extends MinimizableRow<LogReportRow>
{
	private String name;

	public TestCaseRow(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public void addLogEntry(ExecutionLogData.Message mssg)
	{
		LogReportRow logRow = new LogReportRow(mssg.getLogLevel(), mssg.getSource(), mssg.getMessage(), mssg.getTime());
		super.addChild(logRow);
	}
}
