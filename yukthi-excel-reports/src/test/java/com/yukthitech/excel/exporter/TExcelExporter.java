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

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.yukthitech.excel.exporter.data.Cell;
import com.yukthitech.excel.exporter.data.IExcelDataReport;
import com.yukthitech.excel.exporter.data.SimpleExcelDataReport;

/**
 * @author akiran
 *
 */
public class TExcelExporter
{
	@Test
	public void testExcelExport() throws Exception
	{
		IExcelDataReport report = new IExcelDataReport()
		{
			@Override
			public List<List<Cell>> rows()
			{
				return new ArrayList<>(Arrays.asList(
					Arrays.asList(new Cell("rVal1"), new Cell("rVal2"), new Cell("rVal3")),
					Arrays.asList(new Cell("rVal1"), new Cell("rVal2"), new Cell("rVal3")),
					Arrays.asList(new Cell("rVal1"), new Cell("rVal2"), new Cell("rVal3"))
				));
			}
			
			@Override
			public String[] headings()
			{
				return new String[] {"Heading1", "Heading2", "Heading3"};
			}
			
			@Override
			public String getName()
			{
				return "Test";
			}
		};
		
		File tempFile = File.createTempFile("Test", ".xls");
		ExcelExporter exporter = new ExcelExporter();
		
		exporter.generateExcelSheet(tempFile.getPath(), report);
		
		Desktop.getDesktop().open(tempFile);
	}
	
	@Test
	public void testWithSimpleExcelReport() throws Exception
	{
		SimpleExcelDataReport simpleExcelDataReport = new SimpleExcelDataReport("Test", "Heading1", "Heading2", "Heading3");
		simpleExcelDataReport.addRow("rVal1", "rVal2", 20);
		simpleExcelDataReport.addRow("rVal3", "rVal3", 30);
		simpleExcelDataReport.addRow("rVal4", null, 40);
		
		File tempFile = File.createTempFile("Test", ".xls");
		ExcelExporter exporter = new ExcelExporter();
		
		exporter.generateExcelSheet(tempFile.getPath(), simpleExcelDataReport);
		
		Desktop.getDesktop().open(tempFile);
	}
}
