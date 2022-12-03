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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.yukthitech.validation.annotations.Mispattern;

/**
 * Validator for {@link Mispattern} constraint
 * @author akiran
 */
public class MispatternValidator implements ConstraintValidator<Mispattern, String> 
{
	/**
	 * Patterns to be compared
	 */
	private Pattern patterns[];
	
	@Override
	public void initialize(Mispattern constraintAnnotation)
	{
		String regexp[] = constraintAnnotation.regexp();
		
		//compile provided expression
		patterns = new Pattern[regexp.length];
		
		for(int i = 0; i < regexp.length; i++)
		{
			patterns[i] = Pattern.compile(regexp[i]);
		}
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		//if value is null
		if(value == null)
		{
			return true;
		}
		
		//if any of the specified pattern matches fail validation
		for(Pattern pattern : this.patterns)
		{
			if(pattern.matcher(value).matches())
			{
				return false;
			}
		}
		
		return true;
	}

}
