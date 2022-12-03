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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.yukthitech.utils.CommonUtils;

public class ListBean
{
	private List<String> strList;
	
	private Set<Integer> intSet;
	
	private LinkedList<TestDataBean> beanList;
	
	private LinkedList<Object> objectList;

	public List<String> getStrList()
	{
		return strList;
	}

	public void setStrList(List<String> strList)
	{
		this.strList = strList;
	}

	public Set<Integer> getIntSet()
	{
		return intSet;
	}

	public void setIntSet(Set<Integer> intSet)
	{
		this.intSet = intSet;
	}

	public LinkedList<TestDataBean> getBeanList()
	{
		return beanList;
	}

	public void setBeanList(LinkedList<TestDataBean> beanList)
	{
		this.beanList = beanList;
	}

	public LinkedList<Object> getObjectList()
	{
		return objectList;
	}

	public void setObjectList(LinkedList<Object> objectList)
	{
		this.objectList = objectList;
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

		if(!(obj instanceof ListBean))
		{
			return false;
		}

		ListBean other = (ListBean) obj;
		return CommonUtils.isEqual(strList, other.strList)
				&& CommonUtils.isEqual(intSet, other.intSet)
				&& CommonUtils.isEqual(this.beanList, other.beanList)
				&& CommonUtils.isEqual(this.objectList, other.objectList)
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
