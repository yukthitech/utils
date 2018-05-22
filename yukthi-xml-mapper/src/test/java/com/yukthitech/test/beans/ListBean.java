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
