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
