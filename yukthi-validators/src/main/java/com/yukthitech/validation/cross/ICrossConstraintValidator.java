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

import jakarta.validation.ConstraintValidatorContext;

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
	public boolean isValid(ConstraintValidatorContext context, Object bean, Object fieldValue);
}
