package com.yukthitech.autox.ide.views.report;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.autox.exec.report.ExecutionLogData;

/**
 * Represents a test suite row.
 * @author akiran
 */
public class TestSuiteRow extends MinimizableRow<TestCaseRow> implements IReportRow
{
	private String name;
	
	private Map<String, TestCaseRow> nameToTestCase = new HashMap<>();

	public TestSuiteRow(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	@Override
	public void addChild(TestCaseRow child)
	{
		super.addChild(child);
		nameToTestCase.put(child.getName(), child);
	}
	
	public void addLog(String testCase, ExecutionLogData.Message message)
	{
		TestCaseRow testCaseRow = nameToTestCase.get(testCase);
		
		if(testCaseRow == null)
		{
			testCaseRow = new TestCaseRow(testCase);
			addChild(testCaseRow);
		}
		
		testCaseRow.addLogEntry(message);
	}
	
	@Override
	public Object getValueAt(int col)
	{
		return (col == 0) ? name : "";
	}
}
