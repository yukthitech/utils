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

import java.util.Collection;

import com.yukthitech.validation.annotations.NotEmpty;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link NotEmpty} constraint
 * @author akiran
 */
public class NotEmptyValidator implements ConstraintValidator<NotEmpty, Object> 
{
	@Override
	public void initialize(NotEmpty constraintAnnotation)
	{
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context)
	{
		if(value == null)
		{
			return false;
		}
		
		if(value instanceof String)
		{
			return ( ((String)value).trim().length() > 0 );
		}
		
		if(value instanceof Collection)
		{
			return !((Collection)value).isEmpty();
		}
		
		return true;
	}

}
