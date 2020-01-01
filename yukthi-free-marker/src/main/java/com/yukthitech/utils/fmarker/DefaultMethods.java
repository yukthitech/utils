package com.yukthitech.utils.fmarker;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.time.DateUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.annotaion.ExampleDoc;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Default free marker methods.
 * @author akiran
 */
public class DefaultMethods
{
	/**
	 * Object mapper to convert object into json.
	 */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	/**
	 * Converts specified date to string with specified format.
	 * @param date Date to be converted
	 * @param format format to use
	 * @return converted string
	 */
	@FreeMarkerMethod(
			description = "Converts specified date into string in specified format.",
			returnDescription = "Fromated date string.",
			examples = {
				@ExampleDoc(usage = "dateToStr(date, 'MM/dd/yyy')", result = "20/20/2018")
			})
	public static String dateToStr(
			@FmParam(name = "date", description = "Date to be converted") Date date, 
			@FmParam(name = "format", description = "Date format to which date should be converted") String format)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		
		return simpleDateFormat.format(date);
	}
	
	/**
	 * Adds specified number of days to specified date and returns the same.
	 * @param date date to which days needs to be added
	 * @param days days to add
	 * @return result date
	 */
	@FreeMarkerMethod(
			description = "Adds specified number of days to specified date",
			returnDescription = "Resultant date after addition of specified days")
	public static Date addDays(
			@FmParam(name = "date", description = "Date to which days should be added") Date date, 
			@FmParam(name = "days", description = "Days to be added.") int days)
	{
		return DateUtils.addDays(date, days);
	}
	
	@FreeMarkerMethod(
			description = "Adds specified number of hours to specified date",
			returnDescription = "Resultant date after addition of specified hours")
	public static Date addHours(
			@FmParam(name = "date", description = "Date to which hours should be added") Date date, 
			@FmParam(name = "hours", description = "Hours to be added.") int hours)
	{
		return DateUtils.addHours(date, hours);
	}

	@FreeMarkerMethod(
			description = "Adds specified number of minutes to specified date",
			returnDescription = "Resultant date after addition of specified minutes")
	public static Date addMinutes(
			@FmParam(name = "date", description = "Date to which minutes should be added") Date date, 
			@FmParam(name = "minutes", description = "Minutes to be added.") int minutes)
	{
		return DateUtils.addMinutes(date, minutes);
	}

	@FreeMarkerMethod(
			description = "Adds specified number of seconds to specified date",
			returnDescription = "Resultant date after addition of specified seconds")
	public static Date addSeconds(
			@FmParam(name = "date", description = "Date to which seconds should be added") Date date, 
			@FmParam(name = "seconds", description = "Seconds to be added.") int seconds)
	{
		return DateUtils.addSeconds(date, seconds);
	}

	@FreeMarkerMethod(
			description = "Returns the current date object",
			returnDescription = "Current date")
	public static Date today()
	{
		return new Date();
	}

	@FreeMarkerMethod(
			description = "Returns the current date object",
			returnDescription = "Current date and time")
	public static Date now()
	{
		return new Date();
	}

	/**
	 * Converts collection of objects into string.
	 * @param lst list of objects to be converted
	 * @param prefix prefix to be used at the starting.
	 * @param delimiter Delimiter to be used between elements.
	 * @param suffix Suffix to be used at end of string.
	 * @param emptyString String that will be returned if input list is null or empty.
	 * @return result string.
	 */
	@FreeMarkerMethod(
			description = "Converts collection of objects into string.",
			returnDescription = "Converted string",
			examples = {
				@ExampleDoc(usage = "collectionToString(lst, '[', ' | ', ']', '')", result = "[a | b | c]"),
				@ExampleDoc(usage = "collectionToString(null, '[', ' | ', ']', '<empty>')", result = "<empty>")
			})
	public static String collectionToString(
			@FmParam(name = "lst", description = "Collection to be converted") Collection<Object> lst, 
			@FmParam(name = "prefix", description = "Prefix to be used at start of coverted string") String prefix, 
			@FmParam(name = "delimiter", description = "Delimiter to be used between the collection elements") String delimiter, 
			@FmParam(name = "suffix", description = "Suffix to be used at end of converted string") String suffix, 
			@FmParam(name = "emptyString", description = "String to be used when input list is null or empty") String emptyString)
	{
		if(lst == null || lst.isEmpty())
		{
			return emptyString;
		}
		
		StringBuilder builder = new StringBuilder(prefix);
		boolean first = true;
		
		for(Object elem : lst)
		{
			if(!first)
			{
				builder.append(delimiter);
			}
			
			builder.append(elem);
			first = false;
		}
		
		builder.append(suffix);
		return builder.toString();
	}

	/**
	 * Converts map of objects into string.
	 * @param map map of objects to be converted
	 * @param template Template representing how key and value should be converted into string (the string can have #key and #value which will act as place holders)
	 * @param prefix prefix to be used at the starting.
	 * @param delimiter Delimiter to be used between elements.
	 * @param suffix Suffix to be used at end of string.
	 * @param emptyString String that will be returned if input list is null or empty.
	 * @return result string.
	 */
	@FreeMarkerMethod(
			description = "Converts map of objects into string.",
			returnDescription = "Converted string",
			examples = {
				@ExampleDoc(usage = "mapToString(map, '#key=#value', '[', ' | ', ']', '')", result = "[a=1 | b=2 | c=3]"),
				@ExampleDoc(usage = "mapToString(null, '#key=#value', '[', ' | ', ']', '<empty>')", result = "<empty>")
			})
	public static String mapToString(
			@FmParam(name = "map", description = "Prefix to be used at start of coverted string") Map<Object, Object> map, 
			@FmParam(name = "template", description = "Template representing how key and value should be converted "
					+ "into string (the string can have #key and #value which will act as place holders)") String template, 
			@FmParam(name = "prefix", description = "Prefix to be used at start of coverted string") String prefix, 
			@FmParam(name = "delimiter", description = "Delimiter to be used between elements.") String delimiter, 
			@FmParam(name = "suffix", description = "Suffix to be used at end of string.") String suffix, 
			@FmParam(name = "emptyString", description = "String that will be returned if input map is null or empty.") String emptyString)
	{
		if(map == null || map.isEmpty())
		{
			return emptyString;
		}
		
		StringBuilder builder = new StringBuilder(prefix);
		boolean first = true;
		
		for(Entry<Object, Object> entry : map.entrySet())
		{
			if(!first)
			{
				builder.append(delimiter);
			}
			
			builder.append( template.replace("#key", "" + entry.getKey()).replace("#value", "" + entry.getValue()) );
			first = false;
		}
		
		builder.append(suffix);
		return builder.toString();
	}
	
	/**
	 * Converts specified value to string.
	 * @param value value to covert
	 * @return result string.
	 */
	@FreeMarkerMethod(
			value = "toText", 
			description = "Used to convert specified object into string. toString() will be invoked on input object to convert",
			returnDescription = "Converted string. If null, 'null' will be returned.")
	public static String toText(
			@FmParam(name = "value", description = "Value to be converted into string.") Object value)
	{
		if(value == null)
		{
			return "null";
		}
		
		return value.toString();
	}
	
	/**
	 * Converts specified object into json.
	 * @param value value to be converted.
	 * @return converted json
	 */
	@FreeMarkerMethod(
			value = "toJson", 
			description = "Used to convert specified object into json string.",
			returnDescription = "Converted json string.")
	public static String toJson(
			@FmParam(name = "value", description = "Value to be converted into json string.") Object value)
	{
		try
		{
			return OBJECT_MAPPER.writeValueAsString(value);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting value to json", ex);
		}
	}

	/**
	 * Checks if "nullCheck" is null, then this method return first object, if not second object will be 
	 * returned.
	 * @param nullCheck object to be checked for null
	 * @param ifNull object to be returned if null
	 * @param ifNotNull object to be returned if not null
	 * @return ifNull or ifNotNull based on nullCheck
	 */
	@FreeMarkerMethod(
			value = "ifNull", 
			description = "If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.",
			returnDescription = "ifNull or ifNotNull based on nullCheck.")
	public static Object ifNull(
			@FmParam(name = "nullCheck", description = "object to be checked for null") Object nullCheck, 
			@FmParam(name = "ifNull", description = "object to be returned if null") Object ifNull, 
			@FmParam(name = "ifNotNull", description = "object to be returned if not null") Object ifNotNull)
	{
		return (nullCheck == null) ? ifNull : ifNotNull;
	}
	
	/**
	 * Fetches the size of object based on below logic
	 * 	If null, returns 0
	 *  If string, length of string
	 *  If collection, size of collection
	 *  If map, size of map
	 *  otherwise 1
	 *  
	 * @param value value to be checked
	 * @return size of value
	 */
	@FreeMarkerMethod(
			value = "sizeOf", 
			description = "Used to fetch size of specified value. "
					+ "If string length of string is returned, "
					+ "if collection size of collection is returned, "
					+ "if null zero will be returned. "
					+ "Otherwise 1 will be returned.",
			returnDescription = "Size of specified object.")
	@SuppressWarnings("rawtypes")
	public static int sizeOf(
			@FmParam(name = "value", description = "Value whose size to be determined") Object value)
	{
		if(value == null)
		{
			return 0;
		}
		
		if(value instanceof String)
		{
			return ((String) value).length();
		}
		
		if(value instanceof Collection)
		{
			return ((Collection) value).size();
		}
		
		if(value instanceof Map)
		{
			return ((Map) value).size();
		}
		
		return 1;
	}
}
