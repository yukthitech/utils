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
package com.yukthitech.excel.exporter;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.yukthitech.excel.exporter.data.IExcelDataReport;
import com.yukthitech.excel.exporter.style.Border;

public class ExcelExporter
{
	private static final CellCustomizer DEFAULT_CELL_CUSTOMIZER = new CellCustomizer();
	
	static
	{
		DEFAULT_CELL_CUSTOMIZER.setBackground(new Color(200, 200, 200));
		DEFAULT_CELL_CUSTOMIZER.setBorder(new Border(null, BorderStyle.THIN, BorderStyle.THIN, null));
	}
	
	private CellStyle createHeadingCell(InternalWorkbook internalWorkbook, CellCustomizer headingCustomizer)
	{
		HSSFWorkbook wb = internalWorkbook.getWorkbook();
		CellStyle headingStyle = wb.createCellStyle();
		
		headingStyle.setFillBackgroundColor(HSSFColorPredefined.GREY_40_PERCENT.getIndex());
		headingStyle.setAlignment(HorizontalAlignment.CENTER);

		Font font = wb.createFont();
		font.setBold(true);
		headingStyle.setFont(font);

		if(headingCustomizer != null)
		{
			HSSFColor color = internalWorkbook.getColor(headingCustomizer.getBackground());
			
			if(color != null)
			{
				headingStyle.setFillForegroundColor(color.getIndex());
				headingStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			}
			
			if(headingCustomizer.getBorder() != null && headingCustomizer.getBorderColor() != null)
			{
				color = internalWorkbook.getColor(headingCustomizer.getBorderColor());
				headingCustomizer.getBorder().applyToCell(headingStyle, color.getIndex());
			}
		}
		
		return headingStyle;
	}
	
	private void addReport(InternalWorkbook internalWorkbook, CellCustomizer headingCustomizer, IExcelDataReport report)
	{
		HSSFWorkbook wb = internalWorkbook.getWorkbook();
		Sheet sheet = wb.createSheet(report.getName());
		String headings[] = report.headings();
		Cell cell = null;
		int rowIndex = 0;
		
		int headingWidths[] = new int[headings.length];
		
		if(headings != null)
		{
			CellStyle headingStyle = createHeadingCell(internalWorkbook, headingCustomizer);
			Row headingRow = sheet.createRow(rowIndex);
			int headingIndex = 0;
			
			for(String heading: headings)
			{
				cell = headingRow.createCell(headingIndex);
				cell.setCellValue(heading);
				cell.setCellStyle(headingStyle);
				
				headingWidths[headingIndex] = heading.length();
				headingIndex++;
			}
			
			rowIndex++;
		}
		
		List<List<com.yukthitech.excel.exporter.data.Cell>> rows = report.rows();
		
		if(rows != null && !rows.isEmpty())
		{
			Row currentRow = null;
			int cellIndex = 0;
			String cellValue = null;
			
			for(List<com.yukthitech.excel.exporter.data.Cell> row: rows)
			{
				currentRow = sheet.createRow(rowIndex);
				cellIndex = 0;
				
				for(com.yukthitech.excel.exporter.data.Cell dataCell: row)
				{
					cell = currentRow.createCell(cellIndex);
					cellValue = dataCell.getValue();
					
					cell.setCellValue(cellValue);
					//sheet.autoSizeColumn(cellIndex);
				
					if(cellValue != null && headingWidths[cellIndex] < cellValue.length())
					{
						headingWidths[cellIndex] = cellValue.length();
					}
					
					cellIndex++;
				}
				
				rowIndex++;
			}

			//Autosize all the columns as per data content
			List<com.yukthitech.excel.exporter.data.Cell> row = rows.get(0);
			int colCount = row.size();
			
			for(int i = 0; i < colCount; i++)
			{
				if(headingWidths[i] > 150)
				{
					sheet.setColumnWidth(i, 150 * 256);
				}
				else
				{
					sheet.autoSizeColumn(i);
				}
			}
		}
	}
	
	public void generateExcelSheet(String filePath, IExcelDataReport... reports) throws IOException
	{
		generateExcelSheet(filePath, DEFAULT_CELL_CUSTOMIZER, reports);
	}
	
	public void generateExcelSheet(String filePath, CellCustomizer headingCustomizer, IExcelDataReport... reports) throws IOException
	{
		InternalWorkbook internalWorkbook = new InternalWorkbook(new HSSFWorkbook());
		 
		for(IExcelDataReport report: reports)
		{
			addReport(internalWorkbook, headingCustomizer, report);
		}

		FileOutputStream fos = new FileOutputStream(filePath);
		
		internalWorkbook.getWorkbook().write(fos);
		fos.flush();
		
		fos.close();
	}
}
