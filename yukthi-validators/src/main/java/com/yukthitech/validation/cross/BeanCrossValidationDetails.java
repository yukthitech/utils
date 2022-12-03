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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains information about a bean with the fields which has cross validations
 * with cross validation annotation details.
 * 
 * In Javax validation framework, validatiors defined at field level dont have access to the enclosing bean
 * or to other field values. The classes in this package bridges that gap. Having validators at class level may
 * help but looses readability   
 * 
 * @author akiran
 */
public class BeanCrossValidationDetails
{
	/**
	 * Class for which annotations are being loaded
	 */
	private Class<?> beanClass;
	
	/**
	 * Mapping from bean field to list of cross field annotations
	 */
	private Map<Field, List<ICrossConstraintValidator<?>>> fieldToAnnotation = new HashMap<>();

	public BeanCrossValidationDetails(Class<?> beanClass)
	{
		this.beanClass = beanClass;
	}
	
	/**
	 * Adds cross annotation "annotation" defined on field "Field"
	 * @param field
	 * @param validator
	 */
	void addCrossAnnotation(Field field, ICrossConstraintValidator<?> validator)
	{
		List<ICrossConstraintValidator<?>> annotations = fieldToAnnotation.get(field);
		
		if(annotations == null)
		{
			annotations = new ArrayList<>();
			fieldToAnnotation.put(field, annotations);
		}
		
		annotations.add(validator);
	}
	
	/**
	 * Returns bean class for which this instance holds the information
	 * @return
	 */
	public Class<?> getBeanClass()
	{
		return beanClass;
	}
	
	/**
	 * Fetches all the fields with cross validations
	 * @return
	 */
	public Set<Field> getFieldsWithCrossAnnotations()
	{
		return fieldToAnnotation.keySet();
	}
	
	/**
	 * Gets the cross validators defined on specified field
	 * @param field
	 * @return
	 */
	public Collection<ICrossConstraintValidator<?>> getCrossValidators(Field field)
	{
		return fieldToAnnotation.get(field);
	}
}
