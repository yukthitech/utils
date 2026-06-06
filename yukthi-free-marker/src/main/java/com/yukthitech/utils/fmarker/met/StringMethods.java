package com.yukthitech.utils.fmarker.met;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

public class StringMethods
{
	@FreeMarkerMethod(
			description = "Trims input string.",
			returnDescription = "Trimmed value."
			)
	public static String strTrim(
			@FmParam(name = "str", description = "String to be trimmed") String str)
	{
		if(str == null)
		{
			return null;
		}
		
		return str.trim();
	}

	@FreeMarkerMethod(
			description = "Checks if specified substring can be found in main string",
			returnDescription = "true, if substring can be found."
			)
	public static boolean strContains(
			@FmParam(name = "mainString", description = "Main string in which search has to be performed") String mainStr,
			@FmParam(name = "substr", description = "Substring to be searched") String substr,
			@FmParam(name = "ignoreCase", description = "Flag to indicate if case has to be ignored during search", defaultValue = "false") boolean ignoreCase
			)
	{
		if(mainStr == null || substr == null)
		{
			return false;
		}
		
		if(ignoreCase)
		{
			mainStr = mainStr.toLowerCase();
			substr = mainStr.toLowerCase();
		}
		
		return mainStr.contains(substr);
	}

	@FreeMarkerMethod(
			description = "Converts specified string to lower case.",
			returnDescription = "Lower case string.")
	public static String lower(
			@FmParam(name = "str", description = "String to be converted to lower case") String str)
	{
		return str.toLowerCase();
	}

	@FreeMarkerMethod(
			description = "Converts specified string to upper case.",
			returnDescription = "Upper case string.")
	public static String upper(
			@FmParam(name = "str", description = "String to be converted to upper case") String str)
	{
		return str.toUpperCase();
	}

	@FreeMarkerMethod(
			description = "Checks if specified values are equal post string conversion.",
			returnDescription = "True if values are equal."
			)
	public static boolean isEqualString(
			@FmParam(name = "value1", description = "First value to be compared") Object value1,
			@FmParam(name = "value2", description = "Second value to be compared") Object value2)
	{
		String str1 = "" + value1;
		String str2 = "" + value2;
		return str1.equals(str2);
	}

	@FreeMarkerMethod(
			description = "Checks if specified values are equal ignoring case.",
			returnDescription = "True if values are equal ignoring case."
			)
	public static boolean isEqualIgnoreCase(
			@FmParam(name = "value1", description = "First value to be compared") String value1,
			@FmParam(name = "value2", description = "Second value to be compared") String value2)
	{
		return value1.equalsIgnoreCase(value2);
	}
	
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
