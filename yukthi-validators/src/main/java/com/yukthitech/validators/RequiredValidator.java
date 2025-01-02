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

import com.yukthitech.validation.IStringConvertible;
import com.yukthitech.validation.annotations.Required;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Required} constraint
 * @author akiran
 */
public class RequiredValidator implements ConstraintValidator<Required, Object>
{
	@Override
	public void initialize(Required matchWith)
	{
	}
	
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context)
	{
		if(value == null)
		{
			return false;
		}
		
		if((value instanceof String) && ((String)value).trim().length() <= 0)
		{
			return false;
		}
		
		if(value instanceof IStringConvertible)
		{
			String strValue = ((IStringConvertible)value).toStringValue();
			
			if(strValue == null || strValue.trim().length() <= 0)
			{
				return false;
			}
			
			return (strValue.trim().length() > 0 );
		}
		
		if((value instanceof Number) && ((Number)value).longValue() == 0)
		{
			return false;
		}
		
		return true;
	}
}
