package com.yukthitech.excel.importer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Represents state of excel importing.
 * @author akiran
 */
public class ExcelImportState
{
	/**
	 * Current row number.
	 */
	private int rowNo = -1;
	
	/**
	 * Current sheet row iterator.
	 */
	private Iterator<Row> rowIt;
	
	public ExcelImportState(Sheet sheet)
	{
		this.rowIt = sheet.iterator();
	}
	
	public Row nextRow()
	{
		if(!rowIt.hasNext())
		{
			throw new NoSuchElementException("No more rows found to read");
		}
		
		Row row = rowIt.next();
		rowNo++;
		
		return row;
	}
	
	public boolean hasMoreRows()
	{
		return rowIt.hasNext();
	}
	
	public int getRowNo()
	{
		return rowNo;
	}
}
