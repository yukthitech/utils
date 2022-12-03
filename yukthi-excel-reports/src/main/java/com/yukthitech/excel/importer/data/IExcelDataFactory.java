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
package com.yukthitech.excel.importer.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;

/**
 * Controls how data is interpreted from excel. 
 * @author akiran
 * @param <T> type of row data being imported.
 */
public interface IExcelDataFactory<T>
{
	
	/**
	 * Checks if specified row is heading row. Till heading row is found
	 * data rows will not be conisdered.
	 *
	 * @param row the row
	 * @return true, if is heading row
	 */
	public default boolean isHeadingRow(List<String> row)
	{
		return true;
	}
	
	/**
	 * Checks if is specified row is data row.
	 *
	 * @param row the row
	 * @return true, if is data row
	 */
	public default boolean isDataRow(Row row)
	{
		return true;
	}

	public Collection<? extends Column> getColumns();
	public Column getColumn(String name);
	public T newDataObject(Map<String, Object> valueMap);
}
