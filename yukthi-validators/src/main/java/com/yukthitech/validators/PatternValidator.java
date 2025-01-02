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

import java.util.regex.Pattern;

import com.yukthitech.validation.annotations.Mispattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Mispattern} constraint
 * @author akiran
 */
public class PatternValidator implements ConstraintValidator<com.yukthitech.validation.annotations.Pattern, Object> 
{
	/**
	 * Patterns to be compared
	 */
	private Pattern pattern;
	
	@Override
	public void initialize(com.yukthitech.validation.annotations.Pattern constraintAnnotation)
	{
		String regexp = constraintAnnotation.regexp();
		
		//compile provided expression
		pattern = Pattern.compile(regexp);
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context)
	{
		//if value is null
		if(value == null)
		{
			return true;
		}
		
		String strValue = ValidatorUtils.getValue(com.yukthitech.validation.annotations.Pattern.class, value);
		
		//if any of the specified pattern matches fail validation
		if(pattern.matcher(strValue).matches())
		{
			return true;
		}
		
		return false;
	}

}