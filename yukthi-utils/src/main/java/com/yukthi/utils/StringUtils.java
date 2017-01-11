package com.yukthi.utils;

/**
 * String based util methods.
 * @author akiran
 */
public class StringUtils
{
	/**
	 * Converts the first character in input string and returns the same.
	 * @param str String to convert
	 * @return Converted string.
	 */
	public static String toStartLower(String str)
	{
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
}
