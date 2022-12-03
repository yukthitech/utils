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
package com.yukthitech.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to format strings in log4j 2 way.
 * 
 * {} will match with the current index argument. If index is greater than provided values then &lt;undefined&gt; string will be used.
 * {&lt;idx&gt;} can be used to refer to argument at particular index. Helpful in building messages which uses same argument multiple times.
 * 
 * @author akiran
 */
public class MessageFormatter
{
	/**
	 * String that will be used when argument index is out of range
	 */
	public static final String UNDEFINED = "<undefined>";
	
	/**
	 * Param argument pattern
	 */
	private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(\\d*)\\}");
	
	/**
	 * Replaces the args values in "message" using patterns mentioned below and same will be returned. 
	 * 
	 * {} will match with the current index argument. If index is greater than provided values then &lt;undefined&gt; string will be used.
	 * {&lt;idx&gt;} can be used to refer to argument at particular index. Helpful in building messages which uses same argument multiple times.
	 * 
	 * @param message Message string with expressions
	 * @param args Values for expression
	 * @return Formatted string
	 */
	public static String format(String message, Object... args)
	{
		//when message is null, return null
		if(message == null)
		{
			return null;
		}
		
		//when args is null, assume empty values
		if(args == null)
		{
			args = new Object[0];
		}
		
		Matcher matcher = PARAM_PATTERN.matcher(message);
		StringBuffer buffer = new StringBuffer();
		
		int loopIndex = 0;
		int argIndex = 0;
		Object arg = null;
		
		//loop through pattern matches
		while(matcher.find())
		{
			//if index is mentioned in pattern
			if(org.apache.commons.lang3.StringUtils.isNotBlank(matcher.group(1)))
			{
				argIndex = Integer.parseInt(matcher.group(1));
			}
			//if index is not specified, use current loop index
			else
			{
				argIndex = loopIndex;
			}
			
			//if the index is within provided arguments length
			if(argIndex < args.length)
			{
				arg = args[argIndex];
			}
			//if the index is greater than available values
			else
			{
				arg = UNDEFINED;
			}
			
			//if argument value is null
			if(arg == null)
			{
				arg = "null";
			}

			arg = Matcher.quoteReplacement(arg.toString());
			
			matcher.appendReplacement(buffer, arg.toString());
			loopIndex++;
		}
		
		matcher.appendTail(buffer);
		return buffer.toString();
	}
}
