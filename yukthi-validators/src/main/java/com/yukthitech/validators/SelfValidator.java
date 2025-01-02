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

import com.yukthitech.validation.ISelfValidation;
import com.yukthitech.validation.annotations.EnableSelfValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ValidationException;

public class SelfValidator implements ConstraintValidator<EnableSelfValidation, ISelfValidation>
{
	@Override
	public void initialize(EnableSelfValidation constraintAnnotation)
	{
	}

	@Override
	public boolean isValid(ISelfValidation value, ConstraintValidatorContext context)
	{
		try
		{
			((ISelfValidation) value).validate();
		}catch(ValidationException ex)
		{
			context.buildConstraintViolationWithTemplate(ex.getMessage()).addConstraintViolation();
			return false;
		}
		
		return true;
	}
}
