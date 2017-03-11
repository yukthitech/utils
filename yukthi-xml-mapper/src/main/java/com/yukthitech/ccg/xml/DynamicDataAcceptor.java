package com.yukthitech.ccg.xml;

public interface DynamicDataAcceptor
{
	public void add(String propName, Object obj);

	public void add(String propName, String id, Object obj);

	public boolean isIdBased(String propName);
}
