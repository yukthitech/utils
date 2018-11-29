package com.yukthitech.autox.ide.rest;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class TextAreaCellEditor implements TableCellEditor
{
	JTextArea textArea;
	JScrollPane scrollPane;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TextAreaCellEditor()
	{
		textArea = new JTextArea();
		scrollPane = new JScrollPane(textArea);
		
	}
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		textArea.setText(value!=null?value.toString():"");
		return scrollPane;
	}

	@Override
	public Object getCellEditorValue()
	{
		// TODO Auto-generated method stub
		return textArea.getText();
	}

	@Override
	public boolean isCellEditable(EventObject anEvent)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean stopCellEditing()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void cancelCellEditing()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCellEditorListener(CellEditorListener l)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l)
	{
		// TODO Auto-generated method stub
		
	}

	

	
}
