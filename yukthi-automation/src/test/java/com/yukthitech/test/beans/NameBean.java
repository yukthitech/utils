package com.yukthitech.test.beans;

import java.io.Serializable;

public class NameBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String name;

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return name;
	}

}
