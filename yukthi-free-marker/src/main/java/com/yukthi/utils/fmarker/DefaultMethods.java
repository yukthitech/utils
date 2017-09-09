package com.yukthi.utils.fmarker;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.time.DateUtils;

import com.yukthi.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Default free marker methods.
 * @author akiran
 */
public class DefaultMethods
{
	/**
	 * Converts specified date to string with specified format.
	 * @param date Date to be converted
	 * @param format fromat to use
	 * @return converted string
	 */
	@FreeMarkerMethod
	public static String dateToStr(Date date, String format)
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
	@FreeMarkerMethod
	public static Date addDays(Date date, int days)
	{
		return DateUtils.addDays(date, days);
	}
	
	@FreeMarkerMethod
	public static Date today()
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
	@FreeMarkerMethod
	public static String collectionToString(Collection<Object> lst, String prefix, String delimiter, String suffix, String emptyString)
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
	@FreeMarkerMethod
	public static String mapToString(Map<Object, Object> map, String template, String prefix, String delimiter, String suffix, String emptyString)
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
	@FreeMarkerMethod
	public static String toString(Object value)
	{
		if(value == null)
		{
			return "null";
		}
		
		return value.toString();
	}
}
