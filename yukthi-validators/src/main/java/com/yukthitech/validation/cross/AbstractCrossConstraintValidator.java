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
import java.lang.reflect.Method;

/**
 *  Abstract base class for cross constraint validators
 * @author akiran
 */
public abstract class AbstractCrossConstraintValidator<A extends Annotation> implements ICrossConstraintValidator<A> 
{
	/**
	 * Bean type under validation
	 */
	protected Class<?> beanType;
	
	/**
	 * Field under validation
	 */
	protected Field field;
	
	/**
	 * Annotation used for validation
	 */
	protected A annotation;
	
	/**
	 * Default error message defined by annotation
	 */
	protected String errorMessage;
	
	/**
	 * Fetches property value specified by "name" from current annotation. If this is required property, on fail
	 * to fetch value this message will throw exception 
	 * @param name
	 * @param required
	 * @return
	 */
	protected Object getAnnotationProperty(String name, boolean required)
	{
		try
		{
			Method messageMethod = annotation.annotationType().getMethod("message");
			return messageMethod.invoke(annotation);
		}catch(NoSuchMethodException ex)
		{
			return "";
		}catch(Exception ex)
		{
			if(required)
			{
				throw new IllegalStateException("An error occurred while fetching default error message from annotation - " + annotation.annotationType().getName(), ex);
			}
			
			return "";
		}
	}
	
	public Object getAnnotationProperty(String name)
	{
		return getAnnotationProperty(name, false);
	}
	
	@Override
	public final void init(Class<?> beanType, Field field, A annotation)
	{
		this.beanType = beanType;
		this.field = field;
		this.annotation = annotation;
		
		this.init(annotation);
		
		this.errorMessage = "" + getAnnotationProperty("message", true);
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.validation.cross.ICrossConstraintValidator#getErrorMessage()
	 */
	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.validation.cross.ICrossConstraintValidator#getAnnotation()
	 */
	@Override
	public A getAnnotation()
	{
		return annotation;
	}
	
	/**
	 * Child classes are expected to override this methods which wants to initialize 
	 * themselves based on annotation attributes
	 * @param annotation
	 */
	protected void init(A annotation)
	{}
}
