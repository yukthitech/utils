package com.yukthitech.autox.ide.views.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.monitor.MonitorLogMessage;
import com.yukthitech.autox.test.log.LogLevel;

public class ReportTreeTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;

	private final static String[] columnNames = { "", "Message"};
	
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss aa");
	
	private static final String GLOBAL_SETUP = "_global_setup";
	
	private static final String GLOBAL_CLEANUP = "_global_cleanup";

	private List<TestSuiteRow> testSuiteLst = new ArrayList<>();
	
	private Map<String, TestSuiteRow> testSuiteMap = new HashMap<>();

	private List<Object> rows = new ArrayList<>();

	/*
	public void addTestSuiteReport(TestSuiteRow report)
	{
		testSuiteLst.add(report);
		
		int start = rows.size();
		report.populateChildRows(object ->
		{
			rows.add(object);
		});
		
		super.fireTableRowsInserted(start, rows.size() - 1);
	}
	*/
	
	public void reload(ExecutionEnvironment newEnv)
	{
		rows.clear();
		
		for(MonitorLogMessage log : newEnv.getReportMessages())
		{
			addLog(log, false);
		}
		
		refreshRows();
	}
	
	private void addTestSuiteRow(int index, TestSuiteRow testSuiteRow)
	{
		this.testSuiteLst.add(index, testSuiteRow);
		this.testSuiteMap.put(testSuiteRow.getName(), testSuiteRow);
	}
	
	public void addLog(MonitorLogMessage log, boolean refresh)
	{
		String testSuiteName = log.getTestSuite();
		String testCaseName = log.getTestCase();
		String dataName = log.getTestDataName();
		
		if(testSuiteName == null)
		{
			if(log.isSetup())
			{
				testSuiteName = GLOBAL_SETUP;
				testCaseName = "_setup";
			}
			else if(log.isCleanup())
			{
				testSuiteName = GLOBAL_CLEANUP;
				testCaseName = "_cleanup";
			}
			else
			{
				return;
			}
		}
		
		if(dataName != null)
		{
			testCaseName = testCaseName + "[" + dataName + "]";
		}
		
		TestSuiteRow testSuiteRow = testSuiteMap.get(testSuiteName);
		
		if(testSuiteRow == null)
		{
			if(GLOBAL_SETUP.equals(testSuiteName))
			{
				testSuiteRow = new TestSuiteRow(GLOBAL_SETUP);
				addTestSuiteRow(0, testSuiteRow);
			}
			else if(GLOBAL_CLEANUP.equals(testSuiteName))
			{
				testSuiteRow = new TestSuiteRow(GLOBAL_CLEANUP);
				addTestSuiteRow(this.testSuiteLst.size(), testSuiteRow);
			}
			else
			{
				testSuiteRow = new TestSuiteRow(testSuiteName);
				addTestSuiteRow(this.testSuiteLst.size(), testSuiteRow);
			}
		}
		
		testSuiteRow.addLog(testCaseName, log.getMessage());
		
		if(refresh)
		{
			refreshRows();
		}
	}
	
	public synchronized void refreshRows()
	{
		List<Object> newRows = new ArrayList<>(rows.size());
		
		for(TestSuiteRow testSuite : this.testSuiteLst)
		{
			testSuite.populateChildRows(object ->
			{
				newRows.add(object);
			});
		}
		
		this.rows.clear();
		this.rows = newRows;
		
		System.out.println(">>>>>>>>>>>>>>>>>>Refreshing rows with size: " + this.rows.size());
		
		super.fireTableDataChanged();
	}
	
	@Override
	public int getRowCount()
	{
		return rows.size();
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column)
	{
		return columnNames[column];
	}
	
	public Object getRow(int row)
	{
		return rows.get(row);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object row = rows.get(rowIndex);

		if(row instanceof TestSuiteRow)
		{
			TestSuiteRow tsr = (TestSuiteRow) row;
			return (columnIndex == 0) ? tsr.getName() : "";
		}
		else if(row instanceof TestCaseRow)
		{
			TestCaseRow tcr = (TestCaseRow) row;
			return (columnIndex == 0) ? tcr.getName() : "";
		}
		else
		{
			LogReportRow log = (LogReportRow) row;
			
			switch (columnIndex)
			{
				case 0:
				{
					String style = (log.getLogLevel() == LogLevel.ERROR) ? "color:red;" : "";
					
					String str = String.format(
							"<span style='%s'>%s</span> %s [%s]", 
							style, 
							log.getLogLevel().getPaddedString(), 
							TIME_FORMAT.format(log.getTime()), 
							log.getSource(), 
							log.getSource());
					
					return str.replace("'", "\"");
				}
				case 1:
					return log.getMessage();
			}
		}
		
		return "";
	}

}
