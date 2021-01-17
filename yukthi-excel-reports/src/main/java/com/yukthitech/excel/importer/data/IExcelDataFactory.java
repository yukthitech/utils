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
