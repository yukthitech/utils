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
package com.yukthitech.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

public class TransformFmarkerMethods
{
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
			return ITransformConstants.OBJECT_MAPPER.writeValueAsString(value);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting value to json", ex);
		}
	}
	
	@FreeMarkerMethod(
			description = "Convert specified object into boolean value.",
			returnDescription = "Converted value.")
	public static Boolean toBoolean(
			@FmParam(name = "value", description = "Value to be converted.")Object value)
	{
		if(value == null) {return null;}
		
		if(value instanceof Boolean)
		{
			return (Boolean) value;
		}
		
		return "true".equals("" + value);
	}

	@FreeMarkerMethod(
			description = "Convert specified object into int value.",
			returnDescription = "Converted value.")
	public static Integer toInt(
			@FmParam(name = "value", description = "Value to be converted.")Object value)
	{
		if(value == null) {return null;}

		if(value instanceof Integer)
		{
			return (Integer) value;
		}
		
		return Integer.parseInt("" + value);
	}

	@FreeMarkerMethod(
			description = "Convert specified object into long value.",
			returnDescription = "Converted value.")
	public static Long toLong(
			@FmParam(name = "value", description = "Value to be converted.")Object value)
	{
		if(value == null) {return null;}
		
		if(value instanceof Long)
		{
			return (Long) value;
		}
		
		return Long.parseLong("" + value);
	}

	@FreeMarkerMethod(
			description = "Convert specified object into float value.",
			returnDescription = "Converted value.")
	public static Float toFloat(
			@FmParam(name = "value", description = "Value to be converted.")Object value)
	{
		if(value == null) {return null;}
		
		if(value instanceof Float)
		{
			return (Float) value;
		}
		
		return Float.parseFloat("" + value);
	}


	@FreeMarkerMethod(
			description = "Convert specified object into double value.",
			returnDescription = "Converted value.")
	public static Double toDouble(
			@FmParam(name = "value", description = "Value to be converted.")Object value)
	{
		if(value == null) {return null;}
		
		if(value instanceof Double)
		{
			return (Double) value;
		}
		
		return Double.parseDouble("" + value);
	}

	@SuppressWarnings("unchecked")
	@FreeMarkerMethod(
			description = "Wraps specified value with a list, if it is single object",
			returnDescription = "Converted value.")
	public static List<Object> toList(
			@FmParam(name = "value", description = "Value to be converted.")Object value)
	{
		if(value == null)
		{
			return Collections.emptyList();
		}
		
		if(value instanceof Collection)
		{
			return new ArrayList<Object>((Collection<Object>) value);
		}
		
		return Arrays.asList(value);
	}

	@FreeMarkerMethod(
			description = "Simply returns null. Helpful in defining null values in xml",
			returnDescription = "null")
	public static Object nullValue()
	{
		return null;
	}
}
