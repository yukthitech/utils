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

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.yukthitech.validation.annotations.PastOrToday;

/**
 * Validator for {@link PastOrToday} constraint
 * @author akiran
 */
public class PastOrTodayValidator implements ConstraintValidator<PastOrToday, Date>
{
	@Override
	public void initialize(PastOrToday matchWith)
	{
	}
	
	@Override
	public boolean isValid(Date value, ConstraintValidatorContext context)
	{
		//if value is not present
		if(value == null)
		{
			return true;
		}
		
		Date today = new Date();
		return (value.compareTo(today) <= 0);
	}

}
