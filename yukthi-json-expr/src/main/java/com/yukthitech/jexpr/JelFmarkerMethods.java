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
package com.yukthitech.jexpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

public class JelFmarkerMethods
{
	/**
	 * Object mapper for parsing and formatting json.
	 */
	public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

}
