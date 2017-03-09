package com.yukthitech.test.beans;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

public class TestObject
{
	String name;
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String toText(Object obj)
	{
		return name + ":" + obj;
	}
	
	public String toText(NameBean other)
	{
		return name + "=>" + other;
	}

	public void throwError()
	{
		throw new InvalidArgumentException("MESSAGE");
	}
}
