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
package com.yukthitech.test.beans;

import java.util.Map;
import java.util.TreeMap;

import com.yukthitech.utils.CommonUtils;

public class MapBean
{
	private Map<String, String> strMap;
	
	private Map<Integer, Integer> intMap;
	
	private TreeMap<String, TestDataBean> beanMap;
	
	private TreeMap<String, Object> objectMap;

	public Map<String, String> getStrMap()
	{
		return strMap;
	}

	public void setStrMap(Map<String, String> strMap)
	{
		this.strMap = strMap;
	}

	public Map<Integer, Integer> getIntMap()
	{
		return intMap;
	}

	public void setIntMap(Map<Integer, Integer> intMap)
	{
		this.intMap = intMap;
	}

	public TreeMap<String, TestDataBean> getBeanMap()
	{
		return beanMap;
	}

	public void setBeanMap(TreeMap<String, TestDataBean> beanMap)
	{
		this.beanMap = beanMap;
	}

	public TreeMap<String, Object> getObjectMap()
	{
		return objectMap;
	}

	public void setObjectMap(TreeMap<String, Object> objectMap)
	{
		this.objectMap = objectMap;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof MapBean))
		{
			return false;
		}

		MapBean other = (MapBean) obj;
		return CommonUtils.isEqual(intMap, other.intMap)
				&& CommonUtils.isEqual(this.strMap, other.strMap)
				&& CommonUtils.isEqual(this.beanMap, other.beanMap)
				&& CommonUtils.isEqual(this.objectMap, other.objectMap)
				;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return 1;
	}
}
