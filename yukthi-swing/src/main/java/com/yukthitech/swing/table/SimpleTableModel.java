/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.swing.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Represents a simple table model which can be used to set column details and manage row data.
 * @author akranthikiran
 */
public class SimpleTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	
	private List<SimpleTableColumn> columns = new ArrayList<SimpleTableColumn>();
	
	private List<List<Object>> rows = new ArrayList<List<Object>>();
	
	public SimpleTableModel(String... columnNames)
	{
		this(true, columnNames);
	}
	
	public SimpleTableModel(boolean editable, String... columnNames)
	{
		List<SimpleTableColumn> colLst = new ArrayList<SimpleTableColumn>();
		
		for(String col : columnNames)
		{
			colLst.add(new SimpleTableColumn(col, String.class, editable));
		}
		
		this.setColumns(colLst);
	}
	
	public SimpleTableModel(List<SimpleTableColumn> columns)
	{
		setColumns(columns);
	}
	
	private void setColumns(List<SimpleTableColumn> columns)
	{
		this.columns.addAll(columns);
	}

	public void setEditable(boolean editable)
	{
		columns.forEach(col -> col.setEditable(editable));
	}
	
	public void setEditable(int column, boolean editable)
	{
		this.columns.get(column).setEditable(editable);
	}
	
	public void setColumnType(int column, Class<?> type)
	{
		this.columns.get(column).setType(type);
	}
	
	@Override
	public String getColumnName(int column)
	{
		return columns.get(column).getHeading();
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return this.columns.get(column).isEditable();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return this.columns.get(columnIndex).getType();
	}
	
	@Override
	public int getRowCount()
	{
		return rows.size();
	}

	@Override
	public int getColumnCount()
	{
		return this.columns.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return rows.get(rowIndex).get(columnIndex);
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		//TODO: Ensure data is matching with column data type
		rows.get(rowIndex).set(columnIndex, aValue);
	}
	
	public void setRows(List<List<Object>> rows)
	{
		this.rows.clear();
		rows.forEach(row -> SimpleTableModel.this.rows.add(new ArrayList<Object>(row)));
		
		super.fireTableDataChanged();
	}
	
	public void addRow(List<Object> data)
	{
		//TODO: Ensure the length of data and data content is matching with column types
		this.rows.add(new ArrayList<Object>(data));
	
		int newRow = rows.size() - 1;
		super.fireTableRowsInserted(newRow, newRow);
	}
	
	public void insertRow(int index, List<Object> row)
	{
		//TODO: Ensure the length of data and data content is matching with column types
		this.rows.add(index, new ArrayList<Object>(row));
		super.fireTableRowsInserted(index, index);
	}
	
	public void deleteRow(int index)
	{
		rows.remove(index);
		super.fireTableRowsDeleted(index, index);
	}

	public void clear()
	{
		rows.clear();
		super.fireTableDataChanged();
	}
}
