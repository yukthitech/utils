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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating {@link BeanCrossValidationDetails} instances with validator instances
 * @author akiran
 */
public class CrossValidationDetailsFactory
{
	/**
	 * Map to hold Bean details which has cross validators
	 */
	private Map<Class<?>, BeanCrossValidationDetails> validatorDetailsMap = new HashMap<>();
	
	/**
	 * Fetches cross validator details from specified field if any
	 * @param beanType
	 * @param field
	 * @param validationDetails
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void extractCrossValidationDetails(Class<?> beanType, Field field, BeanCrossValidationDetails validationDetails)
	{
		Annotation annotations[] = field.getAnnotations();
		CrossConstraint constraint = null;
		Class<? extends ICrossConstraintValidator<?>> crossValidatorType = null;
		ICrossConstraintValidator<?> crossValidator = null;

		//loop through field annotations
		for(Annotation annotation : annotations)
		{
			constraint = annotation.annotationType().getAnnotation(CrossConstraint.class);

			//if annotation is not cross constraint annotation ignore
			if(constraint == null)
			{
				continue;
			}

			crossValidatorType = constraint.validatedBy();

			//initialize cross validator instance
			try
			{
				crossValidator = crossValidatorType.newInstance();
			}catch(Exception ex)
			{
				throw new IllegalStateException("Failed to create cross validator - " + crossValidatorType.getName(), ex);
			}
			
			((ICrossConstraintValidator)crossValidator).init(beanType, field, annotation);
			
			//add to bean validation details
			validationDetails.addCrossAnnotation(field, crossValidator);
		}

	}

	/**
	 * Creates and returns cross validator of specified type.
	 * 
	 * @param beanType
	 * @return
	 */
	public BeanCrossValidationDetails getCrossValidatorDetails(Class<?> beanType)
	{
		BeanCrossValidationDetails validationDetails = validatorDetailsMap.get(beanType);

		// if validator instance does not exist in cache already
		if(validationDetails != null)
		{
			return validationDetails;
		}

		// create new details object
		validationDetails = new BeanCrossValidationDetails(beanType);

		Class<?> type = beanType;
		Field fields[] = null;

		// loop through the bean hierarchy and extract cross-validation
		// information
		while(type != null)
		{
			// if core java classes are reached break
			if(type.getName().startsWith("java"))
			{
				break;
			}

			fields = type.getDeclaredFields();

			// loop through the fields and extract validation details
			for(Field field : fields)
			{
				extractCrossValidationDetails(beanType, field, validationDetails);
			}

			// goto next parent class
			type = type.getSuperclass();
		}

		//add built details to cache and return
		validatorDetailsMap.put(beanType, validationDetails);
		return validationDetails;
	}

}
