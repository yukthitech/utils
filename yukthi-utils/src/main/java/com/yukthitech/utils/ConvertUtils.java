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

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;

/**
 * Provides wrapper over Apache ConvertUtilsBean. This will throw {@link ConversionException} in case values
 * are not convertible.
 * 
 * @author akiran
 */
public class ConvertUtils
{
	private static ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
	
	static
	{
		convertUtilsBean.register(new ByteConverter(), Byte.class);
		convertUtilsBean.register(new ByteConverter(), byte.class);
		
		convertUtilsBean.register(new ShortConverter(), Short.class);
		convertUtilsBean.register(new ShortConverter(), short.class);
		
		convertUtilsBean.register(new BooleanConverter(), Boolean.class);
		convertUtilsBean.register(new BooleanConverter(), boolean.class);
		
		convertUtilsBean.register(new IntegerConverter(), Integer.class);
		convertUtilsBean.register(new IntegerConverter(), int.class);
		
		convertUtilsBean.register(new LongConverter(), Long.class);
		convertUtilsBean.register(new LongConverter(), long.class);

		convertUtilsBean.register(new FloatConverter(), Float.class);
		convertUtilsBean.register(new FloatConverter(), float.class);
		
		convertUtilsBean.register(new DoubleConverter(), Double.class);
		convertUtilsBean.register(new DoubleConverter(), double.class);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Object convert(Object value, Class<?> targetType)
	{
		if(value == null)
		{
			return null;
		}
		
		if(targetType.isAssignableFrom(value.getClass()))
		{
			return value;
		}
		
		if(Enum.class.isAssignableFrom(targetType))
		{
			if(!(value instanceof String))
			{
				throw new ConversionException(String.format("An error occurred while converting %s (Type: %s) to type - %s", value, value.getClass().getName(), targetType.getName()));
			}
			
			return Enum.valueOf((Class)targetType, (String)value);
		}
		
		return convertUtilsBean.convert(value, targetType);
	}

}
