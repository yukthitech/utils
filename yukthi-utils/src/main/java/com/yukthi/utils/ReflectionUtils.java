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

package com.yukthi.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection related utils
 * @author akiran
 */
public class ReflectionUtils
{
	/**
	 * Fetches annotation from method argument at index "argIdx" of annotation type specified by "annotationType"
	 * @param method Method from which argument annotation needs to be fetched
	 * @param argIdx Argument index from which annotation needs to be fetched
	 * @param annotationType Type of annotation 
	 * @return Annotation of type "A" defined in method parameter at index "argIdx". If not present, null is returned
	 */
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A getParameterAnnotation(Method method, int argIdx, Class<A> annotationType)
	{
		//get all parameter annotations
		Annotation paramAnnotations[][] = method.getParameterAnnotations();
		
		//if no parameter annotations are present
		if(paramAnnotations == null || paramAnnotations.length == 0)
		{
			return null;
		}

		//loop through parameter annotaions
		for(int i = 0; i < paramAnnotations[argIdx].length; i++)
		{
			//if match is found
			if(paramAnnotations[argIdx][i].annotationType().equals(annotationType))
			{
				return (A)paramAnnotations[argIdx][i];
			}
		}
		
		return null;
	}

	/**
	 * Used to set value of specified field irrespective of the field modifier
	 * @param bean Bean from which field value needs to be set
	 * @param field field from which value needs to be set
	 * @param value Value to be set
	 */
	public static void setFieldValue(Object bean, String field, Object value)
	{
		try
		{
			Field fieldObj = bean.getClass().getDeclaredField(field);
			fieldObj.setAccessible(true);
			fieldObj.set(bean, value);
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while fetching field value - " + field, ex);
		}
	}
}
