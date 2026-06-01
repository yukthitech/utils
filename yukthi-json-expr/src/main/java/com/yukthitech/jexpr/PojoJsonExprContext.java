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
package com.yukthitech.jexpr;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Json expr context implementation wrapper over the map.
 * @author Kranthi
 */
public class PojoJsonExprContext extends AbstractMap<String, Object> implements IJsonExprContext
{
	private class PropertyReader
	{
		private Method readMethod;

		public PropertyReader(Method readMethod)
		{
			this.readMethod = readMethod;
		}
	}
	
	private class TransparentEntry implements Entry<String, Object>
	{
		private String key;

		public TransparentEntry(String key)
		{
			this.key = key;
		}
		
		@Override
		public String getKey()
		{
			return key;
		}

		@Override
		public Object getValue()
		{
			return get(key);
		}

		@Override
		public Object setValue(Object value)
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private Map<String, Object> attrMap = new HashMap<>();
	
	private Object pojo;
	
	public PojoJsonExprContext(Object pojo)
	{
		this.pojo = pojo;
		
		try
		{
			PropertyDescriptor propLst[] = Introspector.getBeanInfo(pojo.getClass()).getPropertyDescriptors();
			
			for(PropertyDescriptor prop : propLst)
			{
				if(prop.getReadMethod() == null)
				{
					continue;
				}
				
				attrMap.put(prop.getName(), new PropertyReader(prop.getReadMethod()));
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while reading property info", ex);
		}
	}

	@Override
	public void setValue(String key, Object value)
	{
		attrMap.put(key, value);
	}
	
	@Override
	public Set<String> keySet()
	{
		return attrMap.keySet();
	}

	@Override
	public Set<Entry<String, Object>> entrySet()
	{
		Set<Entry<String, Object>> entrySet = new HashSet<>();
		
		for(Entry<String, Object> entry : attrMap.entrySet())
		{
			entrySet.add(new TransparentEntry(entry.getKey()));
		}
		
		return entrySet;
	}
	
	@Override
	public Object get(Object key)
	{
		Object value = attrMap.get(key);
		
		if(!(value instanceof PropertyReader))
		{
			return value;
		}
		
		PropertyReader reader = (PropertyReader) value;
		
		try
		{
			return reader.readMethod.invoke(pojo);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while invoking read method of property: " + key, ex);
		}
	}
	
	@Override
	public boolean containsKey(Object key)
	{
		return attrMap.containsKey(key);
	}
}
