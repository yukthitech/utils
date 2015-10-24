package com.yukthi.dao.qry.impl;

import com.yukthi.dao.qry.ConnectionSource;

public abstract class AbstractConnectionSource implements ConnectionSource
{
	private String name;

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return super.toString() + "[Name: " + name + "]";
	}
}
