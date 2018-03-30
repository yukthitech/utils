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

package com.yukthitech.utils.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * For internal use only. Used for caching bean properties and mappings
 * for property copying.
 * 
 * @author akiran
 */
public class BeanInfo
{
	/**
	 * Bean type for which this info is being defined
	 */
	private Class<?> beanType;
	
	/**
	 * Map of property information
	 */
	private Map<String, PropertyInfo> properties = new HashMap<String, PropertyInfo>();
	
	/**
	 * Maintains custom mappings. Key would be the source class which needs custom mapping
	 * and value would field to mapping-info which involves custom mapping. 
	 */
	private Map<Class<?>, List<MappingInfo>> customMappings = new HashMap<Class<?>, List<MappingInfo>>();
	
	/**
	 * Instantiates a new bean info.
	 *
	 * @param beanType the bean type
	 */
	public BeanInfo(Class<?> beanType)
	{
		this.beanType = beanType;
	}
	
	/**
	 * Adds property information
	 * @param info property info
	 */
	public void addProperty(PropertyInfo info)
	{
		properties.put(info.getName(), info);
	}
	
	/**
	 * Gets the property info with specified name
	 * @param name Name of the property to fetch
	 * @return Matching property info
	 */
	public PropertyInfo getProperty(String name)
	{
		return properties.get(name);
	}
	
	/**
	 * Fetches property names
	 * @return set of property names
	 */
	public Set<String> getPropertyNames()
	{
		return properties.keySet();
	}

	/**
	 * Gets the bean type for which this info is being defined.
	 *
	 * @return the bean type for which this info is being defined
	 */
	public Class<?> getBeanType()
	{
		return beanType;
	}
	
	/**
	 * Indicates if custom mapping is required when properties are copied from 
	 * specified "fromType"
	 * @param fromType Type to be queried
	 * @return true, if "fromType" requires custom mapping to current bean
	 */
	public boolean needsCustomMapping(Class<?> fromType)
	{
		return customMappings.containsKey(fromType);
	}
	
	public void addCustomMapping(Class<?> forType, MappingInfo mapping)
	{
		List<MappingInfo> mappings = customMappings.get(forType);
				
		if(mappings == null)
		{
			mappings = new ArrayList<MappingInfo>();
			customMappings.put(forType, mappings);
		}
		
		mappings.add(mapping);
		
	}
	
	/**
	 * Fetches custom mapping required by current bean when properties are copied from
	 * "fromType" for field "fieldName"
	 * @param fromType
	 * @return Custom mappings for specified field
	 */
	public List<MappingInfo> getMappings(Class<?> fromType)
	{
		return customMappings.get(fromType);
	}
}
