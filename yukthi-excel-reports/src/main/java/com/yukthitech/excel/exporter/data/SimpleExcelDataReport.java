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
package com.yukthitech.excel.exporter.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple implementation of excel data report.
 * @author akiran
 */
public class SimpleExcelDataReport implements IExcelDataReport
{
	/**
	 * Name of the excel report.
	 */
	private String name;
	
	/**
	 * Headings of excel report.
	 */
	private String headings[];
	
	/**
	 * Rows added to this report.
	 */
	private List<List<Cell>> rows = new ArrayList<>();
	
	/**
	 * Instantiates a new simple excel data report.
	 *
	 * @param name the name
	 * @param headings the headings
	 */
	public SimpleExcelDataReport(String name, String... headings)
	{
		this.name = name;
		this.headings = headings;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String[] headings()
	{
		return headings;
	}

	@Override
	public List<List<Cell>> rows()
	{
		return rows;
	}
	
	/**
	 * Adds specified list of objects as row.
	 * @param row
	 */
	public void addRow(List<Object> row)
	{
		List<Cell> rowCells = new ArrayList<>();
		
		for(Object val : row)
		{
			rowCells.add(new Cell("" + val));
		}
		
		this.rows.add(rowCells);
	}
	
	/**
	 * Adds specified object as row.
	 * @param row
	 */
	public void addRow(Object... row)
	{
		this.addRow(Arrays.asList(row));
	}
}
