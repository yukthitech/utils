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

package com.yukthi.utils.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * @author akiran
 *
 */
public class NestedProperty
{
	/**
	 * List of getters in sequence for this nested property
	 */
	private List<Method> getters = new ArrayList<>();
	
	/**
	 * List of setters in sequence for this nested property
	 */
	private List<Method> setters = new ArrayList<>();
	
	/**
	 * Result type of this nested property
	 */
	private Class<?> type;
	
	/**
	 * Name of the nested property
	 */
	private String name;
	
	/**
	 * Instantiates a new nested property.
	 */
	private NestedProperty()
	{}
	
	/**
	 * Gets the method.
	 *
	 * @param cls the cls
	 * @param name the name
	 * @param paramTypes the param types
	 * @return the method
	 */
	private static Method getMethod(Class<?> cls, String name, Class<?>... paramTypes)
	{
		try
		{
			return cls.getMethod(name, paramTypes);
		}catch(Exception ex)
		{
			return null;
		}
	}
	
	/**
	 * Created nested property instance for specified "property"
	 * @param cls Nested property root type
	 * @param property Property name
	 * @return Nested property instance which can be used to set or get value
	 */
	public static NestedProperty getNestedProperty(Class<?> cls, String property)
	{
		String nestedPropPath[] = property.split("\\.");
		Class<?> prevCls = cls;
		String propName = null;
		Method getter = null, setter = null;
		String currentPath = null;
		int maxIdx = nestedPropPath.length;

		NestedProperty nestedProperty = new NestedProperty();
		
		//loop through property path
		for(int i = 0; i < maxIdx ; i++)
		{
			//keep track of current property path
			currentPath = (currentPath == null) ? nestedPropPath[i] : (currentPath + "." + nestedPropPath[i]);
			
			//compute current property method suffix with first char in upper case
			propName = ("" + nestedPropPath[i].charAt(0)).toUpperCase() + nestedPropPath[i].substring(1);
			
			//get getter at current level
			getter = getMethod(prevCls, "get" + propName);
			
			if(getter == null)
			{
				getter = getMethod(prevCls, "is" + propName);
			}
			
			if(getter == null)
			{
				throw new InvalidArgumentException("No getter found for property - {} [Last class in path - {}]", currentPath, prevCls.getName());
			}
			
			//get setter at current level
			setter = getMethod(prevCls, "set" + propName, getter.getReturnType());
			
			if(setter == null)
			{
				throw new InvalidArgumentException("No setter found for property - {} [Found getter of type - {}] [Last class in path - {}]", 
						currentPath, getter.getReturnType().getName(), prevCls.getName());
			}
			
			//add to result instance and continue to next level
			nestedProperty.setters.add(setter);
			nestedProperty.getters.add(getter);
			
			prevCls = getter.getReturnType();
		}
		
		nestedProperty.type = getter.getReturnType();
		nestedProperty.name = currentPath;
		
		return nestedProperty;
	}

	/**
	 * Gets the result type of this nested property.
	 *
	 * @return the result type of this nested property
	 */
	public Class<?> getType()
	{
		return type;
	}

	/**
	 * Sets the result type of this nested property.
	 *
	 * @param type the new result type of this nested property
	 */
	public void setType(Class<?> type)
	{
		this.type = type;
	}

	/**
	 * Gets the name of the nested property.
	 *
	 * @return the name of the nested property
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the nested property.
	 *
	 * @param name the new name of the nested property
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the current nested property on specified "rootBean" with specified "value"
	 * @param rootBean Bean on which nested property needs to be set
	 * @param value Value to set
	 */
	public void setValue(Object rootBean, Object value)
	{
		int maxIdx = getters.size() - 1;
		Object prevObject = rootBean, newObject = null;
		
		//loop through property path
		for(int i = 0; i <= maxIdx; i++)
		{
			try
			{
				//if end of path is reached, set the final value and break the loop
				if(i == maxIdx)
				{
					setters.get(i).invoke(prevObject, value);
					return;
				}

				newObject = getters.get(i).invoke(prevObject);
				
				//create intermediate beans as needed
				if(newObject == null)
				{
					try
					{
						newObject = getters.get(i).getReturnType().newInstance();
						setters.get(i).invoke(prevObject, newObject);
					}catch(Exception ex)
					{
						throw new InvalidStateException(ex, "Failed to created instance of type - {}", getters.get(i).getReturnType().getName());
					}
				}
				
				prevObject = newObject;
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while setting nested field value - {} on type - {}", name, rootBean.getClass().getName());
			}
		}
	}
	
	/**
	 * Fetches this nested property value from specified "rootBean"
	 * @param rootBean Bean from which nested property needs to be fetched
	 * @return Nested property value. Null will be returned if intermediate bean is not present.
	 */
	public Object getValue(Object rootBean)
	{
		int maxIdx = getters.size() - 1;
		Object prevObject = rootBean, newObject = null;
		
		//loop through property path
		for(int i = 0; i <= maxIdx; i++)
		{
			try
			{
				//if end of path is reached, get the final value and break the loop
				if(i == maxIdx)
				{
					return getters.get(i).invoke(prevObject);
				}

				newObject = getters.get(i).invoke(prevObject);
				
				//if intermediate bean is found null, return null
				if(newObject == null)
				{
					return null;
				}
				
				prevObject = newObject;
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while getting nested field value - {} on type - {}", name, rootBean.getClass().getName());
			}
		}
		
		return null;
	}
}
