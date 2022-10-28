package com.yukthitech.test.beans;

import java.util.List;
import java.util.Objects;

/**
 * Test bean for checking references.
 * @author akranthikiran
 */
public class TestBeanForRef
{
	private String value1;
	
	private List<Integer> value2;
	
	public TestBeanForRef()
	{}
	
	public TestBeanForRef(String value1, List<Integer> value2)
	{
		this.value1 = value1;
		this.value2 = value2;
	}

	public String getValue1()
	{
		return value1;
	}

	public void setValue1(String value1)
	{
		this.value1 = value1;
	}

	public List<Integer> getValue2()
	{
		return value2;
	}

	public void setValue2(List<Integer> value2)
	{
		this.value2 = value2;
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

		if(!(obj instanceof TestBeanForRef))
		{
			return false;
		}

		TestBeanForRef other = (TestBeanForRef) obj;
		return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return Objects.hash(value1, value2);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Value1: ").append(value1);
		
		if(value2 != null)
		{
			builder.append(",").append("Value2: ").append(value2);
		}

		builder.append("]");
		return builder.toString();
	}

}
