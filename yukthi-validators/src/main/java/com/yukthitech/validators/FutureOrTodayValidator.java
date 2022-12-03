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
package com.yukthitech.validators;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.time.DateUtils;

import com.yukthitech.validation.annotations.FutureOrToday;

/**
 * Validator - to validate the target date either future date or today
 * @author akiran
 */
public class FutureOrTodayValidator implements ConstraintValidator<FutureOrToday, Date>
{
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(FutureOrToday matchWith)
	{
	}
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Date targetDate, ConstraintValidatorContext context)
	{
		//if target date is not present, ignore validator
		if(targetDate == null)
		{
			return true;
		}
		
		//compare target date with today
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		targetDate = DateUtils.truncate(targetDate, Calendar.DATE);

		return (targetDate.compareTo(today) >= 0);
	}
}
