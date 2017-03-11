package com.yukthitech.test.beans;

import java.io.Serializable;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

public class TestObject implements Serializable
{
	private static final long serialVersionUID = 1L;
	
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
	
	public String validate(NameBean bean)
	{
		String name = bean.getName();
		
		if(name.length() <= 3)
		{
			throw new InvalidArgumentException("Name should be greater than 3");
		}

		if(name.length() >= 6)
		{
			throw new InvalidArgumentException("Name should be less than 6");
		}
		
		return bean.getName();
	}
}
