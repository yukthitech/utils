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
