package com.yukthitech.autox.ide.rest;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

public class TextAreaCellRenderer implements TableCellRenderer
{
	JScrollPane ScrollPane;
	JTextArea textArea;

	public TextAreaCellRenderer()
	{
		textArea = new JTextArea();
		ScrollPane = new JScrollPane(textArea);
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		// TODO Auto-generated method stub
//		JScrollPane scrollpane = (JScrollPane) value;
//		textArea.setText((String)value);
		return ScrollPane;
	}

}
