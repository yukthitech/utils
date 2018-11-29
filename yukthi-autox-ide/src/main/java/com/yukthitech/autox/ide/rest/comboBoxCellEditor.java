package com.yukthitech.autox.ide.rest;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class comboBoxCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
{

	private String type;
	private List<String> typelist;
	private JComboBox<String> jComboBox=null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public comboBoxCellEditor(List<String> typelist)
	{
		// TODO Auto-generated constructor stub
		this.typelist=typelist;
	}
	
	@Override
	public Object getCellEditorValue()
	{
		// TODO Auto-generated method stub
		return this.type;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		if(value instanceof String) {
			this.type=(String) value;
		}
		JComboBox combo = new JComboBox<String>();
		for(String type:typelist) {
			combo.addItem(type);
		}
		combo.setSelectedItem(type);
		combo.addActionListener(this);
		return jComboBox;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JComboBox<String> combo = (JComboBox<String>) e.getSource();
		this.type=(String) combo.getSelectedItem();
	}

	

}
