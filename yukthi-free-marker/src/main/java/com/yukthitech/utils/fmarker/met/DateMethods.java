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
package com.yukthitech.utils.fmarker.met;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.yukthitech.utils.fmarker.annotaion.ExampleDoc;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

public class DateMethods
{
	@FreeMarkerMethod(
			description = "Converts specified date into millis.",
			returnDescription = "Millis value"
			)
	public static Long toMillis(
			@FmParam(name = "date", description = "Date to be converted") Date date)
	{
		if(date == null)
		{
			return null;
		}
		
		return date.getTime();
	}

	/**
	 * Converts specified date to string with specified format.
	 * @param date Date to be converted
	 * @param format format to use
	 * @return converted string
	 */
	@FreeMarkerMethod(
			description = "Converts specified date into string in specified format.",
			returnDescription = "Fromated date string.",
			examples = {
				@ExampleDoc(usage = "dateToStr(date, 'MM/dd/yyy')", result = "20/20/2018")
			})
	public static String dateToStr(
			@FmParam(name = "date", description = "Date to be converted") Date date, 
			@FmParam(name = "format", description = "Date format to which date should be converted") String format)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		
		return simpleDateFormat.format(date);
	}
	
	/**
	 * Adds specified number of days to specified date and returns the same.
	 * @param date date to which days needs to be added
	 * @param days days to add
	 * @return result date
	 */
	@FreeMarkerMethod(
			description = "Adds specified number of days to specified date",
			returnDescription = "Resultant date after addition of specified days")
	public static Date addDays(
			@FmParam(name = "date", description = "Date to which days should be added") Date date, 
			@FmParam(name = "days", description = "Days to be added.") int days)
	{
		return DateUtils.addDays(date, days);
	}
	
	/**
	 * Adds specified number of years to specified date and returns the same.
	 * @param date date to which years needs to be added
	 * @param years years to add
	 * @return result date
	 */
	@FreeMarkerMethod(
			description = "Adds specified number of days to specified date",
			returnDescription = "Resultant date after addition of specified years")
	public static Date addYears(
			@FmParam(name = "date", description = "Date to which days should be added") Date date, 
			@FmParam(name = "years", description = "Years to be added.") int years)
	{
		return DateUtils.addYears(date, years);
	}
	
	@FreeMarkerMethod(
			description = "Adds specified number of hours to specified date",
			returnDescription = "Resultant date after addition of specified hours")
	public static Date addHours(
			@FmParam(name = "date", description = "Date to which hours should be added") Date date, 
			@FmParam(name = "hours", description = "Hours to be added.") int hours)
	{
		return DateUtils.addHours(date, hours);
	}

	@FreeMarkerMethod(
			description = "Adds specified number of minutes to specified date",
			returnDescription = "Resultant date after addition of specified minutes")
	public static Date addMinutes(
			@FmParam(name = "date", description = "Date to which minutes should be added") Date date, 
			@FmParam(name = "minutes", description = "Minutes to be added.") int minutes)
	{
		return DateUtils.addMinutes(date, minutes);
	}

	@FreeMarkerMethod(
			description = "Adds specified number of seconds to specified date",
			returnDescription = "Resultant date after addition of specified seconds")
	public static Date addSeconds(
			@FmParam(name = "date", description = "Date to which seconds should be added") Date date, 
			@FmParam(name = "seconds", description = "Seconds to be added.") int seconds)
	{
		return DateUtils.addSeconds(date, seconds);
	}

	@FreeMarkerMethod(
			description = "Returns the current date object",
			returnDescription = "Current date")
	public static Date today()
	{
		return new Date();
	}

	@FreeMarkerMethod(
			description = "Returns the current date object",
			returnDescription = "Current date and time")
	public static Date now()
	{
		return new Date();
	}
}
