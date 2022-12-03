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
