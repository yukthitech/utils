package com.yukthitech.autox.ide.rest;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ComboBoxCellRenderer  implements TableCellRenderer
{
	/**   
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<String> combo= new JComboBox<>(new String[] {"select","raw", "file", "template" });


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		combo.setSelectedItem(value);
		return combo;
	}	

}
