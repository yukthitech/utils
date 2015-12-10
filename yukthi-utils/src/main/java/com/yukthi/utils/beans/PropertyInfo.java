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

import java.lang.reflect.Field;

/**
 * Represent property and its type
 * @author akiran
 */
class PropertyInfo
{
	/**
	 * Field representing this property
	 */
	private Field field;
	
	/**
	 * Instantiates a new property info.
	 *
	 * @param field the field
	 */
	public PropertyInfo(Field field)
	{
		this.field = field;
	}
	
	/**
	 * Gets the name of the property.
	 *
	 * @return the name of the property
	 */
	public String getName()
	{
		return field.getName();
	}
	
	/**
	 * Gets the type of the property.
	 *
	 * @return the type of the property
	 */
	public Class<?> getType()
	{
		return field.getType();
	}
	
	/**
	 * Gets the field representing this property.
	 *
	 * @return the field representing this property
	 */
	public Field getField()
	{
		return field;
	}
	
	/**
	 * Sets the value of undelying field on specified bean with specified value
	 * @param bean Bean on which field value needs to be set
	 * @param value Value to be set
	 * @throws IllegalAccessException
	 */
	public void setValue(Object bean, Object value) throws IllegalAccessException
	{
		if(!field.isAccessible())
		{
			field.setAccessible(true);
		}
		
		field.set(bean, value);
	}
	
	/**
	 * Fetches the value of the underlying field from specified bean
	 * @param bean Bean from which field value needs to be fetched
	 * @return Field value
	 * @throws IllegalAccessException
	 */
	public Object getValue(Object bean) throws IllegalAccessException
	{
		if(!field.isAccessible())
		{
			field.setAccessible(true);
		}
		
		return field.get(bean);
	}
}
