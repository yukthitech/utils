/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.yukthi.validation.cross;

import java.lang.reflect.Field;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;

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
