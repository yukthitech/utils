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

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthitech.validation.annotations.PropertyPattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator of {@link PropertyPattern} validation.
 * @author akiran
 */
public class PropertyPatternValidator implements ConstraintValidator<PropertyPattern, Object>
{
	private String property;
	
	private String regexp;

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(PropertyPattern propPattern)
	{
		this.property = propPattern.property();
		this.regexp = propPattern.regexp();
	}
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context)
	{
		//if no value is specified, ignore validation
		if(value == null)
		{
			return true;
		}
		
		// Fetch property value, ignore if not able to fetch prop value
		Object propValue = null;
		
		try
		{
			propValue = PropertyUtils.getProperty(value, property);
		}catch(Exception ex)
		{
			return true;
		}
		
		if(!(propValue instanceof String))
		{
			return true;
		}
		
		return Pattern.matches(regexp, (String) propValue);
	}

}
