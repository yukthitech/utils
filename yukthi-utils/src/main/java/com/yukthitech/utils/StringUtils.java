package com.yukthitech.utils;

/**
 * String based util methods.
 * @author akiran
 */
public class StringUtils
{
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
}
