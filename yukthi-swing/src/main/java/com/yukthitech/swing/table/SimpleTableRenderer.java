package com.yukthitech.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Simple cell rederer that can be used to specify some basic styles based on
 * column number.
 * 
 * @author akranthikiran
 */
public class SimpleTableRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;

	private static Border focusBorder = new LineBorder(Color.black, 2);
	private static Border noFocusBorder = new EmptyBorder(2, 2, 2, 2);

	private Map<Integer, CellStyle> columnStyles = new HashMap<Integer, CellStyle>();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		JComponent component = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		CellStyle style = columnStyles.get(column);

		if(style != null)
		{
			style.customize(component);
			return component;
		}

		super.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
		super.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
		setFont(table.getFont());
		setBorder(hasFocus ? focusBorder : noFocusBorder);

		return component;
	}

	public SimpleTableRenderer setStyle(int colNo, CellStyle style)
	{
		this.columnStyles.put(colNo, style);
		return this;
	}
}
