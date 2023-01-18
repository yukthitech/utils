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
package com.yukthitech.excel.importer;

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
				headings.add(heading);
			}
			
			if(factory.isHeadingRow(row.getRowNum(), headings))
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
		Object cellValue = null;
		Column column = null;
		
		int colCount = columnOrder.size();
		
		for(int i = 0; i < colCount; i++)
		{
			heading = headingIt.next();
			cell = row.getCell(i);
			
			column = factory.getColumn(heading);
			
			if(column == null)
			{
				cellValue = getValue(cell, null, heading);
				map.put(heading, cellValue);
			}
			else
			{
				cellValue = getValue(cell, column.getType(), column.getName());
				
				if((cellValue instanceof String) && column.getType() != ColumnType.STRING)
				{
					cellValue = column.getType().parse((String) cellValue, column.getJavaType(), column.getFormat());
				}
				
				map.put(column.getName(), cellValue);
			}
		}
		
		return map;
	}

	public Object getValue(Cell cell, ColumnType columnType, String columnName)
	{
		if(cell == null)
		{
			return "";
		}
		
		switch(cell.getCellType())
		{
			case BOOLEAN:
			{
				if(columnType == null || columnType == ColumnType.BOOLEAN)
				{
					return cell.getBooleanCellValue();
				}
				
				if(columnType == ColumnType.STRING)
				{
					return "" + cell.getBooleanCellValue();
				}

				throw new IllegalStateException("Boolean cell encountered for column '" + columnName + "'. Expected type: " + columnType);
			}
			case NUMERIC:
			{
				if(columnType == null || columnType == ColumnType.FLOAT)
				{
					return cell.getNumericCellValue();
				}

				if(columnType == ColumnType.DATE)
				{
					return cell.getDateCellValue();
				}
				
				if(columnType == ColumnType.INTEGER)
				{
					return (int) cell.getNumericCellValue();
				}

				if(columnType == ColumnType.INTEGER)
				{
					return DEFAULT_NUMBER_FORMAT.format(cell.getNumericCellValue());
				}

				throw new IllegalStateException("Numeric cell encountered for column '" + columnName + "'. Expected type: " + columnType);
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
