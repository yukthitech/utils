package com.yukthitech.autox.common;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.yukthi.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Default free marker methods.
 * @author akiran
 */
public class DefaultFreeMarkerMethods
{
	/**
	 * Fetches current date.
	 * @return
	 */
	@FreeMarkerMethod
	public static Date now()
	{
		return new Date();
	}
	
	/**
	 * Converts specified date to string with specified format.
	 * @param format fromat to use
	 * @return converted string
	 */
	@FreeMarkerMethod
	public static String dateToStr(String format)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(new Date());
	}
	
	/**
	 * Adds specified number of days to specified date and returns the same.
	 * @param days days to add
	 * @return result date
	 */
	@FreeMarkerMethod
	public static Date addDays(int days)
	{
		return DateUtils.addDays(new Date(), days);
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
}
