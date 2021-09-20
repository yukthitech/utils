package com.yukthitech.ccg.xml.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Utils related to type conversion.
 * @author akranthikiran
 */
public class TypeConversionUtils
{
	/**
	 * Used to parse json value objects.
	 */
	private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Value pattern used for type conversion.
	 */
	private static Pattern PREFIX_PATTERN = Pattern.compile("\\s*([\\w\\[\\]]+)\\s*\\:\\s*(.*)");
	
	/**
	 * Supported date format.
	 */
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private static List<Object> toList(String str, Function<String, Object> converter)
	{
		String tokens[] = str.trim().split("\\s*\\,\\s*");
		List<Object> resLst = new ArrayList<Object>();
		
		for(String token : tokens)
		{
			resLst.add(converter.apply(token));
		}
		
		return resLst;
	}
	
	public static Object strToObject(Object value)
	{
		if(!(value instanceof String))
		{
			return value;
		}
		
		String strValue = (String) value;
		Matcher matcher = PREFIX_PATTERN.matcher(strValue);
		
		if(!matcher.matches())
		{
			return strValue;
		}
		
		String prefix = matcher.group(1);
		String mainValue = matcher.group(2);

		switch (prefix)
		{
			case "int":
			{
				return Integer.parseInt(mainValue);
			}
			case "int[]":
			{
				return toList(mainValue, str -> Integer.parseInt(str));
			}
			case "boolean":
			{
				return "true".equalsIgnoreCase(mainValue);
			}
			case "boolean[]":
			{
				return toList(mainValue, str -> "true".equalsIgnoreCase(str));
			}
			case "long":
			{
				return Long.parseLong(mainValue);
			}
			case "long[]":
			{
				return toList(mainValue, str -> Long.parseLong(str));
			}
			case "string":
			{
				return mainValue;
			}
			case "string[]":
			{
				return toList(mainValue, str -> str);
			}
			case "json":
			{
				try
				{
					return OBJECT_MAPPER.readValue(mainValue, Object.class);
				}catch(Exception ex)
				{
					throw new InvalidArgumentException("Failed to parse json content: {}", mainValue, ex);
				}
			}
			case "date":
			{
				try
				{
					return DATE_FORMAT.parseObject(mainValue);
				}catch(Exception ex)
				{
					throw new InvalidArgumentException("Failed to covert value '{}' into date (expected format: yyyy-MM-dd)", mainValue, ex);
				}
			}
			default:
				throw new UnsupportedOperationException("Unsupported data conversion prefix used: " + prefix);
		}
	}
	
	private static Class<?> getListType(List<Object> lst)
	{
		Class<?> type = null;
		
		for(Object obj : lst)
		{
			if(type == null)
			{
				type = obj.getClass();
			}
			else
			{
				if(!type.equals(obj.getClass()))
				{
					return null;
				}
			}
		}
			
		return type;
	}

	@SuppressWarnings("unchecked")
	public static Object objectToStr(Object object)
	{
		if(object instanceof Integer)
		{
			return "int: " + object;
		}
		else if(object instanceof Long)
		{
			return "long: " + object;
		}
		else if(object instanceof Boolean)
		{
			return "boolean: " + object;
		}
		else if(object instanceof Date)
		{
			return "date: " + DATE_FORMAT.format((Date) object);
		}
		else if(object instanceof String)
		{
			Matcher matcher = PREFIX_PATTERN.matcher((String) object);
			
			if(matcher.matches())
			{
				return "string: " + object;
			}
		}
		else if(object instanceof List)
		{
			List<Object> lst = (List<Object>) object;
			Class<?> type = getListType(lst);
			
			if(Integer.class.equals(type))
			{
				String str = lst.stream().map(obj -> obj.toString()).collect(Collectors.joining(", "));
				return "int[]: " + str;
			}
			else if(Long.class.equals(type))
			{
				String str = lst.stream().map(obj -> obj.toString()).collect(Collectors.joining(", "));
				return "long[]: " + str;
			}
			else if(Boolean.class.equals(type))
			{
				String str = lst.stream().map(obj -> obj.toString()).collect(Collectors.joining(", "));
				return "boolean[]: " + str;
			}
			else if(String.class.equals(type))
			{
				String str = lst.stream().map(obj -> obj.toString()).collect(Collectors.joining(", "));
				return "string[]: " + str;
			}
		}
		
		return object;
	}

}
