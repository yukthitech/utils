package com.yukthitech.autox.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * String related free marker functions.
 * @author akiran
 */
public class StringFreeMarkerFunctions
{
	/**
	 * Used to generate random numbers.
	 */
	private static Random random = new Random(System.currentTimeMillis());
	
	/**
	 * Used to generate unique strings.
	 */
	private static AtomicInteger UQ_NUMBER_TRACKER = new AtomicInteger(1);
	
	@FreeMarkerMethod(
			description = "Finds the first index of specified substring in specified string.",
			returnDescription = "index of subbstring. If not found -1."
			)
	public static int indexOf(
			@FmParam(name = "string", description = "String in which substring needs to be searched") String string,
			@FmParam(name = "substr", description = "Substring that needs to be searched") String substr)
	{
		return string.indexOf(substr);
	}

	@FreeMarkerMethod(
			description = "Finds the last index of specified substring in specified string.",
			returnDescription = "index of subbstring. If not found -1."
			)
	public static int lastIndexOf(
			@FmParam(name = "string", description = "String in which substring needs to be searched") String string,
			@FmParam(name = "substr", description = "Substring that needs to be searched") String substr)
	{
		return string.lastIndexOf(substr);
	}

	@FreeMarkerMethod(
			description = "Converts the specified string to lower case.",
			returnDescription = "Lower cased substring."
			)
	public static String lower(
			@FmParam(name = "string", description = "String to be converted") String string)
	{
		return string.toLowerCase();
	}

	@FreeMarkerMethod(
			description = "Converts the specified string to upper case.",
			returnDescription = "Lower cased substring."
			)
	public static String upper(
			@FmParam(name = "string", description = "String to be converted") String string)
	{
		return string.toUpperCase();
	}

	@FreeMarkerMethod(
			description = "Substring of speicifed string with specified range.",
			returnDescription = "Result substring."
			)
	public static String substr(
			@FmParam(name = "string", description = "String from which substring needs to be extracted") String string,
			@FmParam(name = "start", description = "Start from which substring") int start,
			@FmParam(name = "string", description = "End index of substring. If negative value is specified, this will be not be used.") int end)
	{
		if(end >= 0)
		{
			return string.substring(start, end);
		}
		
		return string.substring(start);
	}
	
	@FreeMarkerMethod(
			description = "Converts specified int value to string using specified radix.",
			returnDescription = "Result substring."
			)
	public static String intToStr(
			@FmParam(name = "value", description = "Int value to be converted") int value,
			@FmParam(name = "radix", description = "Radix to be used for conversion") int radix)
	{
		return Integer.toString(value, radix);
	}
	
	@FreeMarkerMethod(
			description = "Converts specified string to int using specified radix.",
			returnDescription = "Result int value."
			)
	public static int strToInt(
			@FmParam(name = "value", description = "String value to be converted") String value,
			@FmParam(name = "radix", description = "Radix to be used for conversion") int radix)
	{
		return Integer.parseInt(value, radix);
	}

	@FreeMarkerMethod(
			description = "Generates random int.",
			returnDescription = "Random number"
			)
	public static int random()
	{
		return random.nextInt();
	}

	@FreeMarkerMethod(
			description = "Generates random string with specified prefix.",
			returnDescription = "Random string"
			)
	public static String randomString(
			@FmParam(name = "prefix", description = "Prefix that will added to generated random string") String prefix)
	{
		GregorianCalendar today = new GregorianCalendar();
		int dayOfYear = today.get(Calendar.DAY_OF_YEAR);
		int minOfDay = today.get(Calendar.HOUR_OF_DAY) * 60 + today.get(Calendar.MINUTE);
		int minOfYear = dayOfYear * (24 * 60) + minOfDay;

		prefix = prefix + Long.toHexString(minOfYear + random.nextInt(10000)) + Long.toHexString(UQ_NUMBER_TRACKER.incrementAndGet());
		prefix = prefix.toLowerCase();

		return prefix;
	}

	@FreeMarkerMethod(
			description = "Parses string to date using specified format.",
			returnDescription = "Parsed date object"
			)
	public static Date parseDate(
			@FmParam(name = "string", description = "String to parse") String string,
			@FmParam(name = "format", description = "Format to be used for parsing") String format
			)
	{
		SimpleDateFormat simpleDateFormat = null;
		
		try
		{
			simpleDateFormat = new SimpleDateFormat(format);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid date format specified: {}", format, ex);
		}
		
		try
		{
			return simpleDateFormat.parse(string);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Specified date {} is not in specified format: {}", string, format, ex);
		}
	}

	@FreeMarkerMethod(
			description = "Splits the given string into list of strings using specified delimiter.",
			returnDescription = "List of string resulted from spliting."
			)
	public static List<String> split(
			@FmParam(name = "string", description = "String to parse") String string,
			@FmParam(name = "delimiter", description = "Delimiter to be used for spliting") String delimiter
			)
	{
		if(string == null || delimiter == null)
		{
			return null;
		}
		
		String res[] = string.split(delimiter);
		return new ArrayList<>(Arrays.asList(res));
	}
}
