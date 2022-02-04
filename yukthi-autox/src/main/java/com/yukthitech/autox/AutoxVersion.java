package com.yukthitech.autox;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Class to fetch current autox version.
 * @author akiran
 */
public class AutoxVersion
{
	/**
	 * Autox version.
	 */
	private static String version;
	
	static
	{
		try
		{
			InputStream is = AutoxVersion.class.getResourceAsStream("/autox-version.txt");
			version = IOUtils.toString(is, Charset.defaultCharset());
			version = version.trim();
			is.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading autox version", ex);
		}
	}
	
	/**
	 * Gets the autox version.
	 *
	 * @return the autox version
	 */
	public static String getVersion()
	{
		return version;
	}
}
