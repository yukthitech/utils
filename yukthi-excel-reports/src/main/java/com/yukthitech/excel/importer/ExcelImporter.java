package com.yukthitech.excel.importer;

import static com.yukthitech.excel.importer.IExcelImporterConstants.DEFAULT_DATE_FORMAT;
import static com.yukthitech.excel.importer.IExcelImporterConstants.DEFAULT_NUMBER_FORMAT;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.yukthitech.excel.importer.data.Column;
import com.yukthitech.excel.importer.data.ColumnType;
import com.yukthitech.excel.importer.data.IDataDigester;
import com.yukthitech.excel.importer.data.IExcelDataFactory;

public class ExcelImporter
{
	public <T> void importFile(String file, IExcelDataFactory<T> dataFactory, IDataDigester<T> dataDigester)
	{
		File fileObj = new File(file);
		
		try(FileInputStream fis = new FileInputStream(fileObj))
		{
			Workbook wb = WorkbookFactory.create(fis);
			Sheet sheet = wb.getSheetAt(0);
			ExcelImportState excelImportState = new ExcelImportState(sheet);

			List<String> headings = getHeadings(excelImportState, dataFactory);
			
			Row row = null;
			
			T bean = null;
			Map<String, Object> dataMap = null;
			
			while(excelImportState.hasMoreRows())
			{
				row = excelImportState.nextRow();
				
				if(!dataFactory.isDataRow(row))
				{
					continue;
				}
				
				dataMap = toDataMap(row, headings, dataFactory);
				
				bean = dataFactory.newDataObject(dataMap);
				dataDigester.digest(bean);
			}
			
			wb.close();
		}catch(Exception ex)
		{
			throw new IllegalStateException(ex.getMessage(), ex);
		}finally
		{
			
		}
	}

	private List<String> getHeadings(ExcelImportState excelImportState, IExcelDataFactory<?> factory)
	{
		while(excelImportState.hasMoreRows())
		{
			Row row = excelImportState.nextRow();
			Iterator<Cell> cellIt = row.iterator();
			Cell cell = null;
			List<String> headings = new LinkedList<>();
			String heading = null;
			
			while(cellIt.hasNext())
			{
				cell = cellIt.next();
				
				heading = cell.getStringCellValue();
				heading = heading.replaceAll("[\\W\\_]+", "");
				heading = heading.toLowerCase();
				
				headings.add(heading);
			}
			
			if(factory.isHeadingRow(headings))
			{
				return headings;
			}
		}
		
		throw new IllegalStateException("No heading row found.");
	}
	
	private <T> Map<String, Object> toDataMap(Row row, List<String> columnOrder, IExcelDataFactory<T> factory)
	{
		Iterator<String> headingIt = columnOrder.iterator();
		Cell cell = null;
		String heading = null;
		Map<String, Object> map = new HashMap<>();
		String strValue = null;
		Column column = null;
		
		int colCount = columnOrder.size();
		
		for(int i = 0; i < colCount; i++)
		{
			heading = headingIt.next();
			cell = row.getCell(i);
			
			column = factory.getColumn(heading);
			
			if(column == null)
			{
				strValue = getValue(cell, ColumnType.STRING, heading);
				map.put(heading, strValue);
			}
			else
			{
				strValue = getValue(cell, column.getType(), column.getName());
				map.put(column.getName(), column.getType().parse(strValue, column.getJavaType(), column.getFormat()));
			}
		}
		
		return map;
	}

	public String getValue(Cell cell, ColumnType columnType, String columnName)
	{
		if(cell == null)
		{
			return "";
		}
		
		switch(cell.getCellTypeEnum())
		{
			case BOOLEAN:
			{
				if(columnType != ColumnType.BOOLEAN && columnType != ColumnType.STRING)
				{
					throw new IllegalStateException("Boolean cell encountered for column '" + columnName + "'. Expected type: " + columnType);
				}
				
				return "" + cell.getBooleanCellValue();
			}
			case NUMERIC:
			{
				if(columnType == ColumnType.DATE)
				{
					return DEFAULT_DATE_FORMAT.format(cell.getDateCellValue());
				}
				
				if(columnType != ColumnType.FLOAT && columnType != ColumnType.INTEGER && columnType != ColumnType.STRING)
				{
					throw new IllegalStateException("Numeric cell encountered for column '" + columnName + "'. Expected type: " + columnType);
				}
				
				return DEFAULT_NUMBER_FORMAT.format(cell.getNumericCellValue());
			}
			default:
			{
				String cellValue = null;
				
				try
				{
					cellValue = cell.getStringCellValue();
				}catch(Exception ex)
				{
					cellValue = "";
				}
				
				return cellValue;
			}	
		}
	}
}
