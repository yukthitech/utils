package com.yukthi.test.beans;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
}
