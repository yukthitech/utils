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
import com.yukthitech.validation.annotations.MatchWith;
import com.yukthitech.validation.cross.AbstractCrossConstraintValidator;

public class MatchWithValidator extends AbstractCrossConstraintValidator<MatchWith>
{
	private String matchWithField;

	/* (non-Javadoc)
	 * @see com.yukthitech.validation.cross.AbstractCrossConstraintValidator#init(java.lang.annotation.Annotation)
	 */
	@Override
	protected void init(MatchWith annotation)
	{
		this.matchWithField = annotation.field();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.validation.cross.ICrossConstraintValidator#validate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isValid(Object bean, Object fieldValue)
	{
		//obtain other field value
		Object otherValue = null;
		
		try
		{
			otherValue = PropertyAccessor.getProperty(bean, matchWithField);
		}catch(Exception ex)
		{
			throw new IllegalStateException("Invalid/inaccessible property \"" + matchWithField +"\" specified with matchWith validator in bean: " + bean.getClass().getName());
		}
		
		//if current field value is null
		if(fieldValue == null)
		{
			return (otherValue == null);
		}
		
		return fieldValue.equals(otherValue);
	}
}
