package com.yukthi.utils.fmarker;

/**
 * Test bean for testing.
 * @author akiran
 */
public class TestBean
{
	private String name;

	public TestBean(String name)
	{
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("TestBean[");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

}
