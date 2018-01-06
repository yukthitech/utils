package com.yukthitech.autox.test.lang.common;

import java.io.File;

import com.yukthi.utils.fmarker.annotaion.FreeMarkerMethod;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Common free marker methods.
 * @author akiran
 */
public class CommonFreeMarkerMethods
{
	/**
	 * Converts input file path (Can be relative, partial path) to full canonical path.
	 * @param path path to convert.
	 * @return converted path.
	 */
	@FreeMarkerMethod("fullPath")
	public static String fullPath(String path)
	{
		try
		{
			return new File(path).getCanonicalPath();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An exception occurred while fetching full path of path: {}", path, ex);
		}
	}
}
