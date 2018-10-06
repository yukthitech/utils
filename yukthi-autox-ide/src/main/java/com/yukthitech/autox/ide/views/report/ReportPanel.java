package com.yukthitech.autox.ide.views.report;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.exeenv.EnvironmentEvent;
import com.yukthitech.autox.ide.exeenv.EnvironmentEventType;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.ide.views.IViewPanel;
import com.yukthitech.autox.monitor.MonitorLogMessage;
import com.yukthitech.autox.test.log.LogLevel;

@Component
public class ReportPanel extends JPanel implements IViewPanel
{
	private static final long serialVersionUID = 1L;

	@Autowired
	private IdeContext ideContext;

	private JTable jxTreeTable;

	private ReportTreeTableModel model;
	private JScrollPane scrollPane;

	private ExecutionEnvironment activeEnvironment;
	
	private JTabbedPane parentTabbedPane;
	
	/**
	 * Create the panel.
	 */
	public ReportPanel()
	{
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane());
	}
	
	@PostConstruct
	private void init()
	{
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void activeEnvironmentChanged(ExecutionEnvironment activeEnvironment)
			{
				ReportPanel.this.activeEnvironment = activeEnvironment;
				refreshReportLogs();
			}
			
			@Override
			public void environmentChanged(EnvironmentEvent event)
			{
				if(activeEnvironment != event.getEnvironment() || event.getEventType() != EnvironmentEventType.REPORT_LOG_ADDED)
				{
					return;
				}
		
				addNewReportLog(event.getNewReportLog());
			}
		});
	}
	
	private TestSuiteRow createRow(int index)
	{
		TestSuiteRow testSuiteRow = new TestSuiteRow("Test Suite1");
		
		TestCaseRow testCase1 = new TestCaseRow("Test Case1");
		testSuiteRow.addChild(testCase1);
		testCase1.addChild(new LogReportRow(LogLevel.DEBUG, "source", "<h1 style=\"color:blue;\">This is a Heading</h1>\r\n<p>This is a paragraph.</p>", new Date()));
		testCase1.addChild(new LogReportRow(LogLevel.TRACE, "source", "<h1 style=\"color:blue;\">This is a Heading</h1>\r\n<p>This is a paragraph.</p>", new Date()));
		testCase1.addChild(new LogReportRow(LogLevel.ERROR, "source", "<h1 style=\"color:blue;\">This is a Heading</h1>\r\n<p>This is a paragraph.</p>", new Date()));
		
		TestCaseRow testCase2 = new TestCaseRow("Test Case2");
		testSuiteRow.addChild(testCase2);
		testCase2.addChild(new LogReportRow(LogLevel.DEBUG, "source", "<h1 style=\"color:blue;\">This is a Heading</h1>\r\n<p>This is a paragraph.</p>", new Date()));
		testCase2.addChild(new LogReportRow(LogLevel.DEBUG, "source", "<h1 style=\"color:blue;\">This is a Heading</h1>\r\n<p>This is a paragraph.</p>", new Date()));
		testCase2.addChild(new LogReportRow(LogLevel.SUMMARY, "source", "<h1 style=\"color:blue;\">This is a Heading</h1>\r\n<p>This is a paragraph.</p>", new Date()));
		
		return testSuiteRow;
	}

	private JTable getJxTreeTable()
	{
		if(jxTreeTable == null)
		{
			model = new ReportTreeTableModel();
			//model.addTestSuiteReport(createRow(1));
			//model.addTestSuiteReport(createRow(2));
			
			jxTreeTable = new JTable(model);
			MatteBorder border = new MatteBorder(1, 1, 1, 1, UIManager.getColor("Table.gridColor"));
			jxTreeTable.setBorder(border);

			jxTreeTable.getColumnModel().getColumn(0).setCellRenderer(new TreeCellRenderer());
			jxTreeTable.getColumnModel().getColumn(1).setCellRenderer(new HtmlCellRenderer(false));
			jxTreeTable.getColumnModel().getColumn(0).setPreferredWidth(200);
			jxTreeTable.getColumnModel().getColumn(1).setPreferredWidth(800);
			
			jxTreeTable.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if(e.getClickCount() != MouseEvent.BUTTON1)
					{
						return;
					}
					
					int row = jxTreeTable.rowAtPoint(e.getPoint());
					
					if(row >= 0)
					{
						rowClicked(row);
					}
				}
			});

		}
		return jxTreeTable;
	}

	private JScrollPane getScrollPane()
	{
		if(scrollPane == null)
		{
			scrollPane = new JScrollPane(add(getJxTreeTable()));
		}
		return scrollPane;
	}
	
	private void rowClicked(int row)
	{
		Object rowObj = model.getRow(row);
		
		if(rowObj instanceof MinimizableRow)
		{
			((MinimizableRow<?>)rowObj).flipMinimizedStatus();
			model.refreshRows();
		}
	}

	@Override
	public void setParent(JTabbedPane parentTabPane)
	{
		this.parentTabbedPane = parentTabPane;
	}
	
	private void refreshReportLogs()
	{
		model.reload(activeEnvironment);
	}
	
	private void addNewReportLog(MonitorLogMessage logMsg)
	{
		System.out.println(">>>>>>>>>>>>>>>>>>>>>Adding log report: " + logMsg);
		model.addLog(logMsg, true);
	}
}