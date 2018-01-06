package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.ide.engine.IdeEngine;
import com.yukthitech.autox.ide.engine.IdeEngineListener;
import com.yukthitech.autox.ide.model.ExecutedStep;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ContextAttributePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private static ObjectMapper objectMapper = new ObjectMapper();

	private static class JsonCellRenderer extends RSyntaxTextArea implements TableCellRenderer
	{
		private static final long serialVersionUID = 1L;

		public JsonCellRenderer()
		{
			setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
			setCodeFoldingEnabled(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			String strValue = null;

			if(value == null)
			{
				strValue = "";
			}
			else
			{
				try
				{
					strValue = objectMapper.writeValueAsString(value);
				} catch(Exception ex)
				{
					strValue = value.toString();
				}
			}

			if(strValue.length() > 200)
			{
				strValue = strValue.substring(0, 200) + "...";
			}

			super.setText(strValue);
			return this;
		}
	}

	private final JScrollPane scrollPane = new JScrollPane();
	private final JTable table = new JTable();

	private DefaultTableModel defaultTableModel = new DefaultTableModel(new String[] { "Name", "Value" }, 1);

	private IdeEngine ideEngine;
	private final JPanel panel = new JPanel();
	private final JButton btnRefresh = new JButton("Refresh");

	/**
	 * Create the panel.
	 */
	public ContextAttributePanel()
	{
		setLayout(new BorderLayout(0, 0));

		add(scrollPane, BorderLayout.CENTER);

		table.setModel(defaultTableModel);

		table.getColumnModel().getColumn(0).setPreferredWidth(150);

		table.getColumnModel().getColumn(1).setCellRenderer(new JsonCellRenderer());

		scrollPane.setViewportView(table);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		add(panel, BorderLayout.NORTH);
		btnRefresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refreshContextAttributes();
			}
		});

		panel.add(btnRefresh);
	}

	public void setIdeEngine(IdeEngine ideEngine)
	{
		this.ideEngine = ideEngine;

		this.ideEngine.addIdeEngineListener(new IdeEngineListener()
		{
			@Override
			public void stepExecuted(ExecutedStep step)
			{
				refreshContextAttributes();
			}

			@Override
			public void stateLoaded()
			{
				refreshContextAttributes();
			}
		});
	}

	private void refreshContextAttributes()
	{
		// clean current rows
		while(defaultTableModel.getRowCount() > 0)
		{
			defaultTableModel.removeRow(0);
		}

		// add new ones
		TreeMap<String, Object> attr = new TreeMap<>(ideEngine.getContext().getAttr());

		for(String name : attr.keySet())
		{
			defaultTableModel.addRow(new Object[] { name, attr.get(name) });
		}
	}

}
