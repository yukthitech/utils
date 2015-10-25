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

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthi.validation.annotations.MandatoryOption;
import com.yukthi.validation.cross.AbstractCrossConstraintValidator;

/**
 * Validatory for {@link MandatoryOption} constraint
 * @author akiran
 */
public class MandatoryOptionValidator extends AbstractCrossConstraintValidator<MandatoryOption>
{
	/**
	 * Fields with which current field should be compared to
	 */
	private String fields[];

	/* (non-Javadoc)
	 * @see com.yukthi.validation.cross.AbstractCrossConstraintValidator#init(java.lang.annotation.Annotation)
	 */
	@Override
	protected void init(MandatoryOption annotation)
	{
		this.fields = annotation.fields();
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.validation.cross.ICrossConstraintValidator#validate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isValid(Object bean, Object fieldValue)
	{
		//if value is provided for current field
		if(fieldValue != null)
		{
			return true;
		}
		
		Object otherValue = null;
		String field = null;
		
		//check if value is provided for at least one field
		try
		{
			for(String otherField: fields)
			{
				field = otherField;
				otherValue = PropertyUtils.getSimpleProperty(bean, otherField);
				
				if(otherValue != null)
				{
					return true;
				}
			}
		}catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException ex)
		{
			throw new IllegalStateException("Invalid/inaccessible property \"" + field +"\" specified with MandatoryOption validator on field: " + bean.getClass().getName() + "." + super.field.getName());
		}
		
		return false;
	}
}
