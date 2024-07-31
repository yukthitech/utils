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

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * String based util methods.
 * @author akiran
 */
public class StringUtils
{
	private static char[] ALPHA_NUMERIC_CHARS = new char[62];
	
	static
	{
		AtomicInteger index = new AtomicInteger(0);
		addChars('0', '9', index);
		addChars('a', 'z', index);
		addChars('A', 'Z', index);
	}
	
	private static void addChars(char min, char max, AtomicInteger index)
	{
		for(char ch = min; ch <= max; ch++)
		{
			ALPHA_NUMERIC_CHARS[index.getAndIncrement()] = ch;
		}
	}
	
	/**
	 * Converts the first character in input string to lower case and returns the same.
	 * @param str String to convert
	 * @return Converted string.
	 */
	public static String toStartLower(String str)
	{
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
	/**
	 * Converts the first character in input string to upper case and returns the same.
	 * @param str string to be converted
	 * @return converted string
	 */
	public static String toStartUpper(String str)
	{
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	private static boolean isPrintableChar(char ch)
	{
		if(ch == '\n' || ch == '\r' || ch == '\t')
		{
			return true;
		}
		
		return (ch >= 32 && ch <= 126);
	}
	
	public static String removeSpecialCharacters(String str)
	{
		char chArr[] = str.toCharArray();
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < chArr.length; i++)
		{
			if(!isPrintableChar(chArr[i]))
			{
				continue;
			}
			
			builder.append(chArr[i]);
		}
		
		return builder.toString();
	}
	
	public static String randomAlphaNumericString(int length, char... extraChars)
	{
		StringBuilder builder = new StringBuilder();
		Random random = new Random(System.currentTimeMillis());
		
		int charCount = ALPHA_NUMERIC_CHARS.length + extraChars.length;
		int randIdx = 0;
		
		for(int i = 0; i < length; i++)
		{
			randIdx = random.nextInt(charCount);
			
			if(randIdx > ALPHA_NUMERIC_CHARS.length)
			{
				randIdx -= ALPHA_NUMERIC_CHARS.length;
				builder.append(extraChars[randIdx]);
			}
			else
			{
				builder.append(ALPHA_NUMERIC_CHARS[randIdx]);
			}
		}
		
		return builder.toString();
	}
}
