package com.yukthitech.autox.ide.views.report;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class HtmlCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	private boolean minWidth;
	
	public HtmlCellRenderer(boolean minWidth)
	{
		this.minWidth = minWidth;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		String finalHtml = value == null ? "" : "" + value;
		finalHtml = "<html><body>" + finalHtml + "</body></html>";

		label.setText(finalHtml);
		
		//table.setPreferredScrollableViewportSize(table.getPreferredSize());
		int prefHeight = super.getPreferredSize().height;
		int defHeight = table.getRowHeight(row);
		
		if(prefHeight > defHeight)
		{
			table.setRowHeight(row, prefHeight);
		}
		
		return label;
	}

}
