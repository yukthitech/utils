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
package com.yukthitech.utils.fmarker.met;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import com.yukthitech.utils.annotations.Named;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Default free marker methods.
 * @author akiran
 */
@Named("Common Methods")
public class CommonMethods
{
	/**
	 * Used to store value collected in expressions.
	 */
	private static final ThreadLocal<Object> collectorValue = new ThreadLocal<Object>();
	
	@FreeMarkerMethod(
			description = "Collects the value on thread local which can be accessed later. Not meant for external usage.",
			returnDescription = "Empty string")
	public static String __fmarker_collect(
			@FmParam(name = "value", description = "Value to collect") Object value)
	{
		collectorValue.set(value);
		return "";
	}
	
	/**
	 * Fetches the latest value collected. And removes it from memory.
	 * @return latest value
	 */
	public static Object getCollectedValue()
	{
		Object value = collectorValue.get();
		collectorValue.remove();
		
		return value;
	}

	@SuppressWarnings("unchecked")
	@FreeMarkerMethod(
			description = "Used to check if specified value is empty. "
					+ "For collection, map and string, along with null this will check for empty value.",
			returnDescription = "True if value is empty."
			)
	public static boolean isEmpty(
			@FmParam(name = "value", description = "Value to be checked for empty") Object value)
	{
		if(value == null)
		{
			return true;
		}
		
		if(value instanceof String)
		{
			String str = (String) value;
			return (str.trim().length() == 0);
		}
		
		if(value instanceof Collection)
		{
			Collection<Object> col = (Collection<Object>) value;
			return col.isEmpty();
		}

		if(value instanceof Map)
		{
			Map<Object, Object> map = (Map<Object, Object>) value;
			return map.isEmpty();
		}
		
		return false;
	}

	@FreeMarkerMethod(
			description = "Used to check if specified value is not empty. "
					+ "For collection, map and string, along with non-null this will check for non-empty value.",
			returnDescription = "True if value is empty."
			)
	public static boolean isNotEmpty(
			@FmParam(name = "value", description = "Value to be checked for empty") Object value)
	{
		return !isEmpty(value);
	}

	@FreeMarkerMethod(
			description = "Used to check if specified value is null and return approp value when null and when non-null.",
			returnDescription = "Specified null-condition-value or non-null-condition-value."
			)
	public static Object nvl(
			@FmParam(name = "value", description = "Value to be checked for empty") Object value,
			@FmParam(name = "nullValue", description = "Value to be returned when value is null") Object nullValue,
			@FmParam(name = "nonNullValue", description = "Value to be returned when value is non-null") Object nonNullValue
			)
	{
		return (value == null) ? nullValue : nonNullValue;
	}

	@FreeMarkerMethod(
			description = "Used to check if specified value is true and return approp value"
					+ " Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false.",
			returnDescription = "Specified true-condition-value or false-condition-value."
			)
	public static Object ifTrue(
			@FmParam(name = "value", description = "Value to be checked for true.") Object value,
			@FmParam(name = "trueValue", description = "Value to be returned when value is true.", defaultValue = "true") Object trueValue,
			@FmParam(name = "falseValue", description = "Value to be returned when value is false or null.", defaultValue = "false") Object falseValue
			)
	{
		trueValue = (trueValue == null) ? true : trueValue;
		falseValue = (falseValue == null) ? false : falseValue;
		
		boolean bvalue = "true".equalsIgnoreCase("" + value) ? true : false;
		
		return bvalue ? trueValue : falseValue;
	}

	@FreeMarkerMethod(
			description = "Used to check if specified value is false and return approp value"
					+ " Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false. "
					+ "If null, the condition will be considered as false (hence returing falseValue)",
			returnDescription = "Specified true-condition-value or false-condition-value."
			)
	public static Object ifFalse(
			@FmParam(name = "value", description = "Value to be checked for false. Can be boolean true or string 'true'") Object value,
			@FmParam(name = "falseValue", description = "Value to be returned when value is false or null.", defaultValue = "true") Object falseValue,
			@FmParam(name = "trueValue", description = "Value to be returned when value is true.", defaultValue = "false") Object trueValue
			)
	{
		trueValue = (trueValue == null) ? false : trueValue;
		falseValue = (falseValue == null) ? true : falseValue;

		boolean bvalue = "true".equalsIgnoreCase("" + value) ? true : false;
		return bvalue ? trueValue : falseValue;
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
			@FmParam(name = "ifNull", description = "object to be returned if null.", defaultValue = "true (boolean)") Object ifNull, 
			@FmParam(name = "ifNotNull", description = "object to be returned if not null", defaultValue = "false (boolean)") Object ifNotNull)
	{
		ifNull = (ifNull == null) ? true : ifNull;
		ifNotNull = (ifNotNull == null) ? false : ifNotNull;
		
		return (nullCheck == null) ? ifNull : ifNotNull;
	}
	
	@FreeMarkerMethod(
			value = "ifNotNull", 
			description = "If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.",
			returnDescription = "ifNull or ifNotNull based on nullCheck.")
	public static Object ifNotNull(
			@FmParam(name = "nullCheck", description = "object to be checked for null") Object nullCheck, 
			@FmParam(name = "ifNotNull", description = "object to be returned if not null.", defaultValue = "true (boolean)") Object ifNotNull,
			@FmParam(name = "ifNull", description = "object to be returned if null.", defaultValue = "false (boolean)") Object ifNull 
			)
	{
		ifNotNull = (ifNotNull == null) ? true : ifNotNull;
		ifNull = (ifNull == null) ? false : ifNull;
		
		return (nullCheck == null) ? ifNull : ifNotNull;
	}

	/**
	 * If 'nullCheck' is null, 'ifNull' will be returned otherwise 'nullCheck' will be returned.
	 * @param nullCheck
	 * @param ifNull
	 * @return
	 */
	@FreeMarkerMethod(
			value = "nullVal", 
			description = "If 'nullCheck' is null, 'ifNull' will be returned otherwise 'nullCheck' will be returned.",
			returnDescription = "ifNull or nullCheck based on nullCheck is null or not.")
	public static Object nullVal(
			@FmParam(name = "nullCheck", description = "object to be checked for null") Object nullCheck, 
			@FmParam(name = "ifNull", description = "object to be returned if null") Object ifNull)
	{
		return (nullCheck == null) ? ifNull : nullCheck;
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

	/**
	 * Replaces specified substring with replacement in main string.
	 *
	 * @param mainString the main string
	 * @param substring the substring
	 * @param replacement the replacement
	 * @return the string
	 */
	@FreeMarkerMethod(
			value = "replace", 
			description = "Replaces specified substring with replacement in main string.")
	public static String replace(
			@FmParam(name = "mainString", description = "String in which replacement should happen") String mainString,
			@FmParam(name = "substring", description = "Substring to be replaced") String substring,
			@FmParam(name = "replacement", description = "Replacement string") String replacement
			)
	{
		return mainString.replace(substring, replacement);
	}

	/**
	 * Makes first letter of every word into capital letter.
	 *
	 * @param str the str
	 * @return the string
	 */
	@FreeMarkerMethod(
			value = "initcap", 
			description = "Makes first letter of every word into capital letter.")
	public static String initcap(
			@FmParam(name = "str", description = "String to convert") String str
			)
	{
		return WordUtils.capitalize(str);
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
}
