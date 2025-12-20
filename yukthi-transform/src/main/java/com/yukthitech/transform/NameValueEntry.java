package com.yukthitech.transform;

class NameValueEntry
{
	private String name;
	
	private Object value;

	public NameValueEntry(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public Object getValue()
	{
		return value;
	}
}
