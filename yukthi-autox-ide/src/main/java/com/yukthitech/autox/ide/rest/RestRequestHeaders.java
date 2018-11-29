package com.yukthitech.autox.ide.rest;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.model.Header;

@Component
public class RestRequestHeaders extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JScrollPane scrollPane;
	private JTable table;
	private DefaultTableModel haderModel;

	/**
	 * Create the panel.
	 */
	public RestRequestHeaders()
	{
		String columnNames[] = { "Key", "Value" };
		haderModel = new DefaultTableModel(columnNames, 1);
		table = new JTable(haderModel);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				// TODO Auto-generated method stub
				if(table.getSelectedRow() == table.getRowCount() - 1)
				{
					haderModel.addRow(new Object[] { " ", " " });
					table.validate();
				}

			}
		});
		setLayout(new BorderLayout(0, 0));
		table.setCellSelectionEnabled(true);
		add(table);

		scrollPane = new JScrollPane(table);
		add(scrollPane);

	}

	public List<Header> getHeaderList()
	{
		List<Header> headers = new ArrayList<>();
		Header header;

		for(int i = 0; i < haderModel.getRowCount() - 1; i++)
		{
			String name = (String) haderModel.getValueAt(i, 0);
			String value =  (String) haderModel.getValueAt(i, 1);
			System.out.println("name:"+name+"value:"+value);
			if(!name.trim().isEmpty()){
				headers.add(new Header((String)name, (String)value));
			}
		}

		return headers;
	}

}
