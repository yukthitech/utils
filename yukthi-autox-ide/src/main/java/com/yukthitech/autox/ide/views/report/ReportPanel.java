package com.yukthitech.autox.ide.views.report;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
	
	private HtmlCellRenderer htmlCellRenderer = new HtmlCellRenderer(false);
	
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
				htmlCellRenderer.setActiveEnvironment(activeEnvironment);
				
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

	private JTable getJxTreeTable()
	{
		if(jxTreeTable == null)
		{
			model = new ReportTreeTableModel();
			
			jxTreeTable = new JTable(model);
			MatteBorder border = new MatteBorder(1, 1, 1, 1, UIManager.getColor("Table.gridColor"));
			jxTreeTable.setBorder(border);

			jxTreeTable.getColumnModel().getColumn(0).setCellRenderer(new TreeCellRenderer());
			jxTreeTable.getColumnModel().getColumn(1).setCellRenderer(htmlCellRenderer);
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
		model.addLog(logMsg, true);
	}
}