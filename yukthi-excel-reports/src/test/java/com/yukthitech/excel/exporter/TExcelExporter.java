/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
	
}
