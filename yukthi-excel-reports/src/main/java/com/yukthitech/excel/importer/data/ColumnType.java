package com.yukthitech.excel.importer.data;

import static com.yukthitech.excel.importer.IExcelImporterConstants.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public enum ColumnType
{
	STRING 
	{
		public Object parse(String value, Class<?> targetType, String format)
		{
			return value;
		}
	},
	
	DATE
	{
		public Object parse(String value, Class<?> targetType, String format)
		{
			if(StringUtils.isBlank(value))
			{
				return null;
			}
			
			SimpleDateFormat dateFormat = StringUtils.isBlank(format) ? DEFAULT_DATE_FORMAT : new SimpleDateFormat(format);
			
			try
			{
				return dateFormat.parse(value);
			}catch(ParseException ex)
			{
				throw new IllegalArgumentException("Invalid date value specified: " + value);
			}
		}
	},
	
	INTEGER
	{
		public Object parse(String value, Class<?> targetType, String format)
		{
			value = value.replace(",", "");

			if(StringUtils.isBlank(value))
			{
				return null;
			}
			
			double doubleVal = 0;
			
			try
			{
				doubleVal = DEFAULT_NUMBER_FORMAT.parse(value).doubleValue();
			}catch(ParseException ex)
			{
				throw new IllegalArgumentException("Invalid numeric value spcified for integer type: " + value);
			}
			
			if(byte.class.equals(targetType) || Byte.class.equals(targetType))
			{
				return (byte)doubleVal;
			}
			
			if(short.class.equals(targetType) || Short.class.equals(targetType))
			{
				return (short)doubleVal;
			}
			
			if(long.class.equals(targetType) || Long.class.equals(targetType))
			{
				return (long)doubleVal;
			}
			
			return (int)doubleVal;
		}
	},
	
	FLOAT
	{
		public Object parse(String value, Class<?> targetType, String format)
		{
			value = value.replace(",", "");
			
			if(StringUtils.isBlank(value))
			{
				return null;
			}
			
			double doubleVal = 0;
			
			try
			{
				doubleVal = DEFAULT_NUMBER_FORMAT.parse(value).doubleValue();
			}catch(ParseException ex)
			{
				throw new IllegalArgumentException("Invalid numeric value spcified for integer type: " + value);
			}
			
			if(float.class.equals(targetType) || Float.class.equals(targetType))
			{
				return (float)doubleVal;
			}
			
			return doubleVal;
		}
	},
	
	BOOLEAN
	{
		public Object parse(String value, Class<?> targetType, String format)
		{
			return "true".equalsIgnoreCase(value);
		}
	},
	
	ENUM
	{
		public Object parse(String value, Class<?> targetType, String format)
		{
			if(StringUtils.isBlank(value))
			{
				return null;
			}
			
			Map<String, Object> map = getEnumMap(targetType);
			return map.get(value.toLowerCase());
		}
	};
	
	private static Map<Class<?>, Map<String, Object>> typeToEnumMap = new HashMap<>();
	
	public Map<String, Object> getEnumMap(Class<?> enumType)
	{
		Map<String, Object> enumMap = typeToEnumMap.get(enumType);
		
		if(enumMap != null)
		{
			return enumMap;
		}
		
		enumMap = new HashMap<>();
		Object enums[] = enumType.getEnumConstants();
		
		for(Object enumObj : enums)
		{
			enumMap.put(((Enum<?>)enumObj).name().toLowerCase(), enumObj);
		}
		
		typeToEnumMap.put(enumType, enumMap);
		return enumMap;
	}
	
	public abstract Object parse(String value, Class<?> targetType, String format);
	
}
