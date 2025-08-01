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
package com.yukthitech.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Reflection related utils
 * 
 * @author akiran
 */
public class ReflectionUtils
{
	/**
	 * Fetches annotation from method argument at index "argIdx" of annotation
	 * type specified by "annotationType"
	 * 
	 * @param method
	 *            Method from which argument annotation needs to be fetched
	 * @param argIdx
	 *            Argument index from which annotation needs to be fetched
	 * @param annotationType
	 *            Type of annotation
	 * @return Annotation of type "A" defined in method parameter at index
	 *         "argIdx". If not present, null is returned
	 */
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A getParameterAnnotation(Method method, int argIdx, Class<A> annotationType)
	{
		// get all parameter annotations
		Annotation paramAnnotations[][] = method.getParameterAnnotations();

		// if no parameter annotations are present
		if(paramAnnotations == null || paramAnnotations.length == 0)
		{
			return null;
		}

		// loop through parameter annotaions
		for(int i = 0; i < paramAnnotations[argIdx].length; i++)
		{
			// if match is found
			if(paramAnnotations[argIdx][i].annotationType().equals(annotationType))
			{
				return (A) paramAnnotations[argIdx][i];
			}
		}

		return null;
	}

	/**
	 * Used to set value of specified field irrespective of the field modifier
	 * 
	 * @param bean
	 *            Bean from which field value needs to be set
	 * @param field
	 *            field from which value needs to be set
	 * @param value
	 *            Value to be set
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
			throw new IllegalStateException("An error occurred while seting field value - " + field, ex);
		}
	}

	/**
	 * Used to set value of specified static field irrespective of the field modifier
	 * 
	 * @param bean
	 *            Bean from which field value needs to be set
	 * @param field
	 *            static field from which value needs to be set
	 * @param value
	 *            Value to be set
	 */
	public static void setStaticFieldValue(Class<?> clazz, String field, Object value)
	{
		try
		{
			Field fieldObj = clazz.getDeclaredField(field);
			fieldObj.setAccessible(true);
			fieldObj.set(null, value);
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while seting static field value - " + field, ex);
		}
	}
	
	/**
	 * Invokes specified method on specified bean with specified params irrespective of modifiers of method.
	 * @param bean bean on which method to be invoked. For static methods this can be null
	 * @param method method to be invoked. Can be obtained using method {@link #getMethod(Class, String, Class...)}
	 * @param params params to be passed to method
	 * @return method return value.
	 */
	public static Object invokeMethod(Object bean, Method method, Object... params)
	{
		try
		{
			method.setAccessible(true);
			return method.invoke(bean, params);
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while invoking method - " + method.getName(), ex);
		}
	}

	/**
	 * Used to set value of specified field irrespective of the field modifier
	 * 
	 * @param bean
	 *            Bean from which field value needs to be set
	 * @param field
	 *            field from which value needs to be set
	 * @param value
	 *            Value to be set
	 */
	public static void setFieldValue(Object bean, Field field, Object value)
	{
		try
		{
			field.setAccessible(true);
			field.set(bean, value);
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while seting field value - " + field.getName(), ex);
		}
	}

	/**
	 * Used to fetch the field value of specified bean.
	 * 
	 * @param bean
	 *            Bean from whose field value needs to be fetched.
	 * @param field
	 *            Field whose value needs to be fetched.
	 * @return Specified field value.
	 */
	public static Object getFieldValue(Object bean, String field)
	{
		try
		{
			Field fieldObj = bean.getClass().getDeclaredField(field);
			fieldObj.setAccessible(true);
			return fieldObj.get(bean);
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while geting field value - " + field, ex);
		}
	}

	/**
	 * Fetches type of the nested field type
	 * 
	 * @param cls
	 *            Class in which nested field type needs to be fetched
	 * @param fieldName
	 *            Nested field name whose type needs to be fetched
	 * @return Nested field type
	 */
	public static Class<?> getNestedFieldType(Class<?> cls, String fieldName)
	{
		String nestedPropPath[] = fieldName.split("\\.");
		int maxIdx = nestedPropPath.length - 1;
		Field field = null;
		Class<?> prevCls = cls;

		// loop through property path
		for(int i = 0; i <= maxIdx; i++)
		{
			try
			{
				// get intermediate property descriptor
				try
				{
					field = prevCls.getDeclaredField(nestedPropPath[i]);
				} catch(Exception ex)
				{
					field = null;
				}

				// if the property is not found or found as read only, return
				// false
				if(field == null)
				{
					return null;
				}

				// if end of path is reached, set the final value and break the
				// loop
				if(i == maxIdx)
				{
					return field.getType();
				}

				prevCls = field.getType();
			} catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while fetching nested field type - {}", fieldName, ex);
			}
		}

		return null;
	}

	public static Object getNestedFieldValue(Object bean, String fieldName)
	{
		if(bean == null)
		{
			return null;
		}

		String nestedPropPath[] = fieldName.split("\\.");
		int maxIdx = nestedPropPath.length - 1;
		Field field = null;
		Object prevObject = bean;

		// loop through property path
		for(int i = 0; i <= maxIdx; i++)
		{
			try
			{
				// get intermediate property descriptor
				try
				{
					field = prevObject.getClass().getDeclaredField(nestedPropPath[i]);
				} catch(Exception ex)
				{
					field = null;
				}

				// if the property is not found or found as read only, return
				// false
				if(field == null)
				{
					return null;
				}

				field.setAccessible(true);

				// if end of path is reached, set the final value and break the
				// loop
				if(i == maxIdx)
				{
					return field.get(prevObject);
				}

				prevObject = field.get(prevObject);
			} catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while fetching nested field value - {} on type - {}", fieldName, bean.getClass().getName(), ex);
			}
		}

		return null;
	}

	public static void setNestedFieldValue(Object bean, String fieldName, Object value)
	{
		if(bean == null)
		{
			return;
		}

		String nestedPropPath[] = fieldName.split("\\.");
		int maxIdx = nestedPropPath.length - 1;
		Field field = null;
		Object prevObject = bean, newObject = null;

		// loop through property path
		for(int i = 0; i <= maxIdx; i++)
		{
			try
			{
				// get intermediate property descriptor
				try
				{
					field = prevObject.getClass().getDeclaredField(nestedPropPath[i]);
				} catch(Exception ex)
				{
					field = null;
				}

				// if the property is not found or found as read only, return
				// false
				if(field == null)
				{
					throw new InvalidArgumentException("Invalid nested field '{}' specified for bean type - {}", fieldName, bean.getClass().getName());
				}

				field.setAccessible(true);

				// if end of path is reached, set the final value and break the
				// loop
				if(i == maxIdx)
				{
					field.set(prevObject, value);
					return;
				}

				newObject = field.get(prevObject);

				// create intermediate beans as needed
				if(newObject == null)
				{
					try
					{
						newObject = field.getType().newInstance();
						field.set(prevObject, newObject);
					} catch(Exception ex)
					{
						throw new InvalidStateException("Failed to created instance of type - {}", field.getType().getName());
					}
				}

				prevObject = newObject;
			} catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while fetching nested field value - {} on type - {}", fieldName, bean.getClass().getName(), ex);
			}
		}
	}

	/**
	 * String representation of the specified method. For example string
	 * conversion of this method will be <BR>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;toString(java.lang.reflect.Method)
	 * 
	 * @param met
	 *            Method whose string representation is needed.
	 * @return String representation of met.
	 */
	public static String toString(Method met)
	{
		if(met == null)
		{
			return null;
		}

		StringBuilder res = new StringBuilder(met.getName() + "(");
		Class<?> arg[] = met.getParameterTypes();

		if(arg != null && arg.length > 0)
		{
			for(int i = 0; i < arg.length; i++)
			{
				res.append(arg[i].getName());

				if(i < arg.length - 1)
				{
					res.append(",");
				}
			}
		}
		
		return res.append(")").toString();
	}
	
	/**
	 * Creates proxy instance of specified annotation.
	 * @param annotationType annotation type whose proxy instance to be created
	 * @param values values to be reflected
	 * @return annotation proxy instance
	 */
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A createProxyAnnotation(Class<A> annotationType, Map<String, Object> values)
	{
		final Map<String, Object> finalValues = new HashMap<String, Object>(values);
		
		Method methods[] = annotationType.getMethods();
		Object value = null;
		
		for(Method method : methods)
		{
			if(finalValues.get(method.getName()) != null)
			{
				continue;
			}
			
			value = method.getDefaultValue();
			
			if(value != null)
			{
				finalValues.put(method.getName(), value);
			}
		}
		
		InvocationHandler handler = new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				return finalValues.get(method.getName());
			}
		};
		
		A newInstance = (A) Proxy.newProxyInstance(ReflectionUtils.class.getClassLoader(), new Class[] {annotationType}, handler);
		return newInstance;
	}
	
	public static Method getMethod(Class<?> type, String name, Class<?>... paramTypes)
	{
		try
		{
			return type.getMethod(name, paramTypes);
		}catch(NoSuchMethodException ex)
		{
			return null;
		}
	}
}
