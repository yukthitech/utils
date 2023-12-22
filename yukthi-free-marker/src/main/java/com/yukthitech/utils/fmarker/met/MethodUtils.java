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
package com.yukthitech.utils.fmarker.met;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yukthitech.utils.ConvertUtils;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class MethodUtils
{
	/**
	 * Converts the specified argument into required type.
	 * @param argument Argument value to be converted
	 * @param requiredType Expected type
	 * @return converted object value
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T convertArgument(Object argument, Class<T> requiredType) throws TemplateModelException
	{
		if(argument == null)
		{
			return null;
		}
		
		if(argument instanceof TemplateModel)
		{
			argument = DeepUnwrap.unwrap((TemplateModel)argument);
		}
		
		if(requiredType.isAssignableFrom(argument.getClass()))
		{
			return (T) argument;
		}
		
		if(argument instanceof Collection)
		{
			if(List.class.isAssignableFrom(requiredType))
			{
				return (T) new ArrayList( (Collection) argument );
			}
			else if(Set.class.isAssignableFrom(requiredType))
			{
				return (T) new HashSet( (Collection) argument );
			}
			else if(Collection.class.isAssignableFrom(requiredType))
			{
				return (T)  argument ;
			}
		}
		
		return (T) ConvertUtils.convert(argument, requiredType);
	}

}
