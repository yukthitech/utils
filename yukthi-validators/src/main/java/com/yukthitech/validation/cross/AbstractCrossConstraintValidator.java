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
