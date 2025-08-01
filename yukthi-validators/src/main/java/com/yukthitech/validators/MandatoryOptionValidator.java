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

import com.yukthitech.utils.PropertyAccessor;
import com.yukthitech.validation.annotations.MandatoryOption;
import com.yukthitech.validation.cross.AbstractCrossConstraintValidator;

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
	 * @see com.yukthitech.validation.cross.AbstractCrossConstraintValidator#init(java.lang.annotation.Annotation)
	 */
	@Override
	protected void init(MandatoryOption annotation)
	{
		this.fields = annotation.fields();
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.validation.cross.ICrossConstraintValidator#validate(java.lang.Object, java.lang.Object)
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
				otherValue = PropertyAccessor.getProperty(bean, otherField);
				
				if(otherValue != null)
				{
					return true;
				}
			}
		}catch(Exception ex)
		{
			throw new IllegalStateException("Invalid/inaccessible property \"" + field +"\" specified with MandatoryOption validator on field: " + bean.getClass().getName() + "." + super.field.getName());
		}
		
		return false;
	}
}
