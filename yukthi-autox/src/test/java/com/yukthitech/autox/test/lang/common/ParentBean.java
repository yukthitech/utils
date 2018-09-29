package com.yukthitech.autox.test.lang.common;

public class ParentBean
{
	private Subbean subbean;
	
	private ParentBean parentBean;

	public Subbean getSubbean()
	{
		return subbean;
	}

	public void setSubbean(Subbean subbean)
	{
		this.subbean = subbean;
	}

	public ParentBean getParentBean()
	{
		return parentBean;
	}

	public void setParentBean(ParentBean parentBean)
	{
		this.parentBean = parentBean;
	}
}
