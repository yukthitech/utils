package com.yukthitech.autox.test;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.ccg.xml.DynamicBean;

/**
 * Represents data for test case execution. 
 * @author akiran
 */
public class TestCaseData
{
	/**
	 * String representation of the data which will be appended to test case name.
	 */
	private String name;
	
	/**
	 * Value of this test case data.
	 */
	private Object value;
	
	/**
	 * Instantiates a new test case data.
	 */
	public TestCaseData()
	{}
	
	/**
	 * Instantiates a new test case data.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public TestCaseData(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets the string representation of the data which will be appended to test case name.
	 *
	 * @return the string representation of the data which will be appended to test case name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the string representation of the data which will be appended to test case name.
	 *
	 * @param name the new string representation of the data which will be appended to test case name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the value of this test case data.
	 *
	 * @return the value of this test case data
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the value of this test case data.
	 *
	 * @param value the new value of this test case data
	 */
	public void setValue(Object value)
	{
		this.value = AutomationUtils.parseObjectSource(AutomationContext.getInstance(), null, value, null);
	}
	
	/**
	 * Convenient method to set dynamic value for test data from xml.
	 * @param value
	 */
	public void setDynamicValue(DynamicBean value)
	{
		this.value = value.toSimpleMap();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(getClass().getSimpleName());
		builder.append("[");

		builder.append("Name: ").append(name);
		builder.append(",").append("Value: ").append(value);

		builder.append("]");
		return builder.toString();
	}

}
