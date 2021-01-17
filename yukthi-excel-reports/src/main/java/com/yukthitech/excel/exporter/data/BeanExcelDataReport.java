package com.yukthitech.excel.exporter.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.excel.importer.data.ExcelIgnoreField;
import com.yukthitech.excel.importer.data.ExcelLabel;

/**
 * Simple implementation of excel data report.
 * @author akiran
 */
public class BeanExcelDataReport<T> implements IExcelDataReport
{
	/**
	 * Name of the excel report.
	 */
	private String name;
	
	/**
	 * Row bean type.
	 */
	private Class<?> rowType;
	
	/**
	 * Rows added to this report.
	 */
	private List<Object> rows = new LinkedList<>();
	
	/**
	 * Headings to be used.
	 */
	private List<String> headings;
	
	/**
	 * Instantiates a new simple excel data report.
	 *
	 * @param name the name
	 * @param headings the headings
	 */
	public BeanExcelDataReport(String name, Class<T> rowType, List<T> beans)
	{
		this.name = name;
		
		if(beans != null)
		{
			this.rows.addAll(beans);
		}
		
		this.rowType = rowType;
	}
	
	public void addRow(T bean)
	{
		this.rows.add(bean);
	}
	
	public void removeRow(T bean)
	{
		this.rows.remove(bean);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String[] headings()
	{
		if(this.headings != null)
		{
			return headings.toArray(new String[0]);
		}
		
		headings = new ArrayList<>();
		
		Field fields[] = rowType.getDeclaredFields();
		ExcelLabel excelLabel = null;
		ExcelIgnoreField ignoreField = null;
		
		for(Field field : fields)
		{
			if(Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}
			
			ignoreField = field.getAnnotation(ExcelIgnoreField.class);
			
			if(ignoreField != null)
			{
				continue;
			}
			
			excelLabel = field.getAnnotation(ExcelLabel.class);
			
			if(excelLabel != null)
			{
				headings.add(excelLabel.value());
			}
			else
			{
				headings.add(field.getName());
			}
		}
		
		return headings.toArray(new String[0]);
	}
	
	private String getCellValue(Object bean, Field field) throws Exception
	{
		Object val = field.get(bean);
		
		if(val == null)
		{
			return "";
		}
		
		ExcelLabel excelLabel = field.getAnnotation(ExcelLabel.class);
		
		if(excelLabel != null && StringUtils.isNotBlank(excelLabel.format()))
		{
			if(val instanceof Date)
			{
				Date date = (Date) val;
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(excelLabel.format());
				
				val = simpleDateFormat.format(date);
			}
			else if(val instanceof Number)
			{
				double doubleNo = ((Number) val).doubleValue();
				
				DecimalFormat decimalFormat = new DecimalFormat(excelLabel.format());
				val = decimalFormat.format(doubleNo);
			}
		}
		
		return val.toString();
	}

	@Override
	public List<List<Cell>> rows()
	{
		List<List<Cell>> rows = new ArrayList<>(this.rows.size());
		List<Cell> row = null;
		
		Field fields[] = rowType.getDeclaredFields();
		ExcelIgnoreField ignoreField = null;
		
		for(Object bean : this.rows)
		{
			row = new ArrayList<>(fields.length);
			
			for(Field field : fields)
			{
				if(Modifier.isStatic(field.getModifiers()))
				{
					continue;
				}
				
				ignoreField = field.getAnnotation(ExcelIgnoreField.class);
				
				if(ignoreField != null)
				{
					continue;
				}
				
				try
				{
					field.setAccessible(true);
					
					row.add(new Cell(getCellValue(bean, field)));
				}catch(Exception ex)
				{
					throw new IllegalStateException("An exception occurred while fetching field values", ex);
				}
			}
			
			rows.add(row);
		}
		
		return rows;
	}
}
