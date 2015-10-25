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
package com.yukthi.validators;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.yukthi.validation.annotations.GreaterThan;
import com.yukthi.validation.cross.AbstractCrossConstraintValidator;

/**
 * Validator - Validator for {@link GreaterThan} constraint
 * @author akiran
 */
public class GreaterThanValidator extends AbstractCrossConstraintValidator<GreaterThan>
{
	/**
	 * Field whose value should be compared with
	 */
	private String greaterThanField;
	
	/* (non-Javadoc)
	 * @see com.yukthi.validation.cross.AbstractCrossConstraintValidator#init(java.lang.annotation.Annotation)
	 */
	@Override
	protected void init(GreaterThan annotation)
	{
		this.greaterThanField = annotation.field();
	}

	/* (non-Javadoc)
	 * @see com.yukthi.validation.cross.ICrossConstraintValidator#validate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isValid(Object bean, Object fieldValue)
	{
		//obtain field value to be compared
		Object otherValue = null;
		
		try
		{
			otherValue = PropertyUtils.getSimpleProperty(bean, greaterThanField);
		}catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException ex)
		{
			throw new IllegalStateException("Invalid/inaccessible property \"" + greaterThanField +"\" specified with matchWith validator in bean: " + bean.getClass().getName());
		}
		
		//if other value is null or of different type
		if(fieldValue == null || otherValue == null || !fieldValue.getClass().equals(otherValue.getClass()))
		{
			return true;
		}
		
		//number comparison
		if(otherValue instanceof Number)
		{
			return (((Number)fieldValue).doubleValue() > ((Number)otherValue).doubleValue());
		}
		
		//date comparison
		if(otherValue instanceof Date)
		{
			Date dateValue = DateUtils.truncate((Date)fieldValue, Calendar.DATE) ;
			Date otherDateValue = DateUtils.truncate((Date)otherValue, Calendar.DATE);
			
			return (dateValue.compareTo(otherDateValue) > 0);
		}
		
		return true;
	}
}
