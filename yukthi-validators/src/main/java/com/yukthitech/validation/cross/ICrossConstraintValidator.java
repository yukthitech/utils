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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Cross constraint validator definition that can be used for validation. For each cross-annotation defined
 * on a field, an instance of the cross validator will be created. And {@link #isValid(Object, Object)} method will be invoked
 * for field value validation of beans of same type. 
 * 
 * @author akiran
 *
 * @param <A> Supported cross constraint annotation 
 */
public interface ICrossConstraintValidator<A extends Annotation>
{
	/**
	 * Cross constraint annotation defined on the field which can 
	 * be used to extract values defined in annotation. This method will be called only once during initialization
	 * @param beanType Bean type in which the target annotation is used
	 * @param field Field on which annotation is used
	 * @param annotation Annotation instance used
	 */
	public void init(Class<?> beanType, Field field, A annotation);
	
	/**
	 * Returns the annotation tied to this validator
	 * @return
	 */
	public A getAnnotation();
	
	/**
	 * Gets the property value of the 
	 * @param name
	 * @return
	 */
	public Object getAnnotationProperty(String name);
	
	/**
	 * Provides error message to be used when validation fails
	 * @return
	 */
	public String getErrorMessage();
	
	/**
	 * Called by this validation framework to validate "fieldValue" of specified bean
	 * @param bean Bean for which field value is being validated
	 * @param fieldValue Field value to be validated
	 */
	public boolean isValid(Object bean, Object fieldValue);
}
