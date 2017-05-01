package com.yukthitech.autox.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.yukthi.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Default free marker methods.
 * @author akiran
 */
public class DefaultFreeMarkerMethods
{
	/**
	 * Fetches current date.
	 * @return
	 */
	@FreeMarkerMethod
	public static Date now()
	{
		System.out.println("===========>now is called");
		return new Date();
	}
	
	/**
	 * Converts specified date to string with specified format.
	 * @param format fromat to use
	 * @return converted string
	 */
	@FreeMarkerMethod
	public static String dateToStr(String format)
	{
		System.out.println("===========>dateToStr is called");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(new Date());
	}
	
	/**
	 * Adds specified number of days to specified date and returns the same.
	 * @param days days to add
	 * @return result date
	 */
	@FreeMarkerMethod
	public static Date addDays(int days)
	{
		System.out.println("===========>addDays is called");
		return DateUtils.addDays(new Date(), days);
	}
}
