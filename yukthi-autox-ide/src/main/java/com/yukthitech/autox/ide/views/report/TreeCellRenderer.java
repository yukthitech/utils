package com.yukthitech.autox.ide.views.report;

import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.yukthitech.autox.ide.IdeUtils;

public class TreeCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	private static final EmptyBorder DEF_BORDER = new EmptyBorder(3, 3, 3, 3);
	private static final EmptyBorder TEST_CASE_BORDER = new EmptyBorder(3, 10, 3, 3);
	private static final EmptyBorder LOG_BORDER = new EmptyBorder(3, 30, 3, 3);
	
	private static ImageIcon MINIMIZED_ICON  = IdeUtils.loadIcon("/ui/icons/minimized.png", 10);
	private static ImageIcon EXPANDED_ICON  = IdeUtils.loadIcon("/ui/icons/expanded.png", 10);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Object rowBean = ((ReportTreeTableModel)table.getModel()).getRow(row);
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		label.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
		label.setBorder(DEF_BORDER);
		
		if(rowBean instanceof MinimizableRow)
		{
			if(rowBean instanceof TestCaseRow)
			{
				label.setBorder(TEST_CASE_BORDER);
			}
			
			MinimizableRow<?> rowVal = (MinimizableRow<?>) rowBean;
			
			if(rowVal.isMinimized())
			{
				label.setIcon(MINIMIZED_ICON);
			}
			else
			{
				label.setIcon(EXPANDED_ICON);
			}
		}
		else
		{
			String finalHtml = value == null ? "" : "" + value;
			finalHtml = "<html><body>" + finalHtml + "</body></html>";

			label.setIcon(null);
			label.setBorder(LOG_BORDER);
			label.setText(finalHtml);
		}
		
		return label;
	}

}
