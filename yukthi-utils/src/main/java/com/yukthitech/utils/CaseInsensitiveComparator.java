package com.yukthitech.utils;

import java.util.Comparator;

/**
 * Comparator to compare strings in case insensitive way.
 * @author akiran
 */
public class CaseInsensitiveComparator implements Comparator<String>
{
	@Override
	public int compare(String o1, String o2)
	{
		return o1.compareToIgnoreCase(o2);
	}
}
