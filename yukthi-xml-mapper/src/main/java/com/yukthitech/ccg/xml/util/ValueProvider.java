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
package com.yukthitech.ccg.xml.util;

import java.util.Map;

import com.yukthitech.utils.PropertyAccessor;

public interface ValueProvider
{
	public class MapValueProvider implements ValueProvider
	{
		private Map<String, ?> nameToValue;

		public MapValueProvider(Map<String, ?> nameToValue)
		{
			if(nameToValue == null)
				throw new NullPointerException("Map nameToValue can not be null.");

			this.nameToValue = nameToValue;
		}

		public Object getValue(String name)
		{
			return nameToValue.get(name);
		}
	}

	public class PropertyValueProvider implements ValueProvider
	{
		private Object rootBean;

		public PropertyValueProvider(Object bean)
		{
			this.rootBean = bean;
		}

		@Override
		public Object getValue(String name)
		{
			try
			{
				return PropertyAccessor.getProperty(rootBean, name);
			} catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while invoking property " + name + " on bean " + rootBean.getClass().getName(), ex);
			}
		}

	}

	public Object getValue(String name);
}
