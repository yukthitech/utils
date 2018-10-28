package com.yukthitech.autox.ide.contextAttribute;

import javax.swing.table.DefaultTableModel;

public class ContextAttributeTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 1L;

	boolean[] columnEditables = new boolean[] { false, false };
	private static String columnNames[] = new String[] { "Key", "Value" };

	public ContextAttributeTableModel()
	{
		super(columnNames, 0);
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return columnEditables[column];
	}
}
