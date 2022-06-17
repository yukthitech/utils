package com.yukthitech.excel.exporter.data;

public class Cell
{
	private String value;
	
	private int rowspan = 1;
	
	public Cell(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public int getRowspan() 
	{
		return rowspan;
	}

	public void setRowspan(int rowspan) 
	{
		this.rowspan = rowspan;
	}
}
