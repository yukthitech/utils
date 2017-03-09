package com.yukthitech.test.beans;

public class SimpleBean
{
	private String name;

	public void setName(String name)
	{
		this.name = name;
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
