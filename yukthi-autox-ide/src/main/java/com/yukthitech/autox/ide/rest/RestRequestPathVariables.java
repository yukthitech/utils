package com.yukthitech.autox.ide.rest;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.model.Header;
import com.yukthitech.autox.ide.model.PathVariable;

@Component
public class RestRequestPathVariables extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JTable table;

	private DefaultTableModel pathVariableModel;
	private JScrollPane scrollPane;

	public RestRequestPathVariables()
	{
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane(), BorderLayout.NORTH);
	}

	private JTable getTable()
	{
		if(table == null)
		{
			table = new JTable();
			pathVariableModel = new DefaultTableModel(new Object[][] {}, new String[] { "Key", "Value" })
			{
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				boolean[] columnEditables = new boolean[] { false, true };

				public boolean isCellEditable(int row, int column)
				{
					return columnEditables[column];
				}
			};

			table.setModel(pathVariableModel);
		}
		return table;
	}

	private JScrollPane getScrollPane()
	{
		if(scrollPane == null)
		{
			scrollPane = new JScrollPane(getTable());
		}
		return scrollPane;
	}

	public JTable getTableQueryParams()
	{
		return table;
	}

	public void addQueryParam(String url)
	{
		Pattern p = Pattern.compile("\\{(.*?)\\}");
		Matcher m = p.matcher(url);
		for(int i=0;i<pathVariableModel.getRowCount();i++){
			pathVariableModel.removeRow(i);
		}
		while(m.find())
		{
			pathVariableModel.addRow(new Object[] { m.group(1), "" });
			System.out.println(m.group(1));
		}
		pathVariableModel.fireTableDataChanged();

	}

	public List<PathVariable> getPathVariables()
	{
		List<PathVariable> pathVariables = new ArrayList<>();
		Header header;

		for(int i = 0; i < pathVariableModel.getRowCount(); i++)
		{
			Object name = pathVariableModel.getValueAt(i, 0);
			Object value = pathVariableModel.getValueAt(i, 1);
			if(name!=null && !((String)name).isEmpty()){
				if(value==null){
					value= "";
				}
				pathVariables.add(new PathVariable((String)name, (String)value));
			}
		}
		return pathVariables;
	}
}
