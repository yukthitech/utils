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
package com.yukthitech.validation.cross;

import java.lang.reflect.Field;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validation;

/**
 * Javax Validator which in turn enables cross validation on the bean
 * @author akiran
 *
 */
public class CrossValidationEnabler implements ConstraintValidator<EnableCrossValidation, Object>
{
	/**
	 * Factory for creating alidation details
	 */
	private CrossValidationDetailsFactory crossValidationDetailsFactory = new CrossValidationDetailsFactory();
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(EnableCrossValidation constraintAnnotation)
	{
	}

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(Object bean, ConstraintValidatorContext context)
	{
		//get bean cross validation details
		BeanCrossValidationDetails beanCrossValidationDetails = crossValidationDetailsFactory.getCrossValidatorDetails(bean.getClass());
		
		Object fieldValue = null;
		boolean defaultConstraintEnabled = true;
		boolean isValid = true;
		String message = null;
		
		MessageInterpolator mssgInterpolator =  Validation.buildDefaultValidatorFactory().getMessageInterpolator();
		
		//loop through fields which has cross validation constraints
		for(Field field : beanCrossValidationDetails.getFieldsWithCrossAnnotations())
		{
			//fetch field value
			field.setAccessible(true);
			
			try
			{
				fieldValue = field.get(bean);
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while fetching field value - " + bean.getClass().getName() + "." + field.getName());
			}
			
			//loop through cross constraint of the validators
			for(ICrossConstraintValidator<?> validator : beanCrossValidationDetails.getCrossValidators(field))
			{
				//if cross validation failed
				if(!validator.isValid(bean, fieldValue))
				{
					//if an error message is getting added, disable default message
					if(defaultConstraintEnabled)
					{
						context.disableDefaultConstraintViolation();
						defaultConstraintEnabled = false;
					}
					
					//add cross validation error message
					message = mssgInterpolator.interpolate(validator.getErrorMessage(), new CrossMessageInterpolatorContext(bean, validator));
					
					//message = processMessageParameters(validator.getErrorMessage(), validator);
					context.buildConstraintViolationWithTemplate(message).addNode(field.getName()).addConstraintViolation();
					isValid = false;
				}
			}
		}
		
		return isValid;
	}
}
