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
package com.yukthitech.validators;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.yukthitech.utils.PropertyAccessor;
import com.yukthitech.validation.annotations.GreaterThanEquals;
import com.yukthitech.validation.cross.AbstractCrossConstraintValidator;

public class GreaterThanEqualsValidator extends AbstractCrossConstraintValidator<GreaterThanEquals>
{
	private String greaterThanField;

	/* (non-Javadoc)
	 * @see com.yukthitech.validation.cross.AbstractCrossConstraintValidator#init(java.lang.annotation.Annotation)
	 */
	@Override
	protected void init(GreaterThanEquals annotation)
	{
		this.greaterThanField = annotation.field();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.validation.cross.ICrossConstraintValidator#validate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isValid(Object bean, Object fieldValue)
	{
		if(bean == null)
		{
			return true;
		}
		
		//fetch other field value
		Object otherValue = null;
		
		try
		{
			otherValue = PropertyAccessor.getProperty(bean, greaterThanField);
		}catch(Exception ex)
		{
			throw new IllegalStateException("Invalid/inaccessible property \"" + greaterThanField +"\" specified with matchWith validator in bean: " + bean.getClass().getName());
		}
		
		//ensure other field value is present and is of same type
		if(fieldValue == null || otherValue == null || !fieldValue.getClass().equals(otherValue.getClass()))
		{
			return true;
		}
		
		//number comparison
		if(otherValue instanceof Number)
		{
			return (((Number)fieldValue).doubleValue() >= ((Number)otherValue).doubleValue());
		}
		
		//date comparison
		if(otherValue instanceof Date)
		{
			Date dateValue = DateUtils.truncate((Date)fieldValue, Calendar.DATE);
			Date otherDateValue = DateUtils.truncate((Date)otherValue, Calendar.DATE);
			
			return (dateValue.compareTo(otherDateValue) >= 0);
		}
		
		return true;
	}
}
