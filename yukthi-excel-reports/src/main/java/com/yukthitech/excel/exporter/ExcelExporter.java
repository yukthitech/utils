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
				
				headingIndex++;
			}
			
			rowIndex++;
		}
		
		List<List<com.yukthitech.excel.exporter.data.Cell>> rows = report.rows();
		
		if(rows != null && !rows.isEmpty())
		{
			Row currentRow = null;
			int cellIndex = 0;
			
			for(List<com.yukthitech.excel.exporter.data.Cell> row: rows)
			{
				currentRow = sheet.createRow(rowIndex);
				cellIndex = 0;
				
				for(com.yukthitech.excel.exporter.data.Cell dataCell: row)
				{
					cell = currentRow.createCell(cellIndex);
					cell.setCellValue(dataCell.getValue());
					//sheet.autoSizeColumn(cellIndex);
				
					cellIndex++;
				}
				
				rowIndex++;
			}

			//Autosize all the columns as per data content
			List<com.yukthitech.excel.exporter.data.Cell> row = rows.get(0);
			int colCount = row.size();
			int colWidth = 0;
			
			for(int i = 0; i < colCount; i++)
			{
				sheet.autoSizeColumn(i);
				colWidth = (int)(sheet.getColumnWidth(i) * 1.5);

				try
				{
					sheet.setColumnWidth(i, colWidth);
				}catch(IllegalArgumentException ex)
				{
					//if the column length is too long, IllegalArgumentException will come 
					// and is handled to max length
					sheet.setColumnWidth(i, 254);
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
