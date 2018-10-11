package com.yukthitech.autox.ide.views.report;

import com.yukthitech.autox.test.log.ExecutionLogData;

public class TestCaseRow extends MinimizableRow<LogReportRow> implements IReportRow
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
		String mssgHtml = mssg.getMessage();
		
		if(mssg instanceof ExecutionLogData.ImageMessage)
		{
			ExecutionLogData.ImageMessage imgMssg = (ExecutionLogData.ImageMessage) mssg;
			mssgHtml += String.format("<br/><img src='./logs/%s' style='width: 200px; border: 1px solid black;'/>", imgMssg.getImageFileName());
		}
		
		LogReportRow logRow = new LogReportRow(mssg.getLogLevel(), mssg.getSource(), mssgHtml, mssg.getTime());
		super.addChild(logRow);
	}
	
	@Override
	public Object getValueAt(int col)
	{
		return (col == 0) ? name : "";
	}
}
