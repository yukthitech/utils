package com.yukthitech.autox.test;

import java.io.Serializable;

import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Parameter that can be passed during step group execution.
 * @author akiran
 */
public class FunctionParam implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the group param.
	 */
	private String name;
	
	/**
	 * Value of the param.
	 */
	@Param(description = "Value of the parameter", sourceType = SourceType.EXPRESSION)
	private Object value;
	
	public FunctionParam()
	{}
	
	public FunctionParam(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets the name of the group param.
	 *
	 * @return the name of the group param
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the group param.
	 *
	 * @param name the new name of the group param
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the value of the param.
	 *
	 * @return the value of the param
	 */
	public Object getValue()
	{
		return value;
	}
	
	/**
	 * Sets the value of the param.
	 *
	 * @param value the new value of the param
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("{").append(name).append("=").append(value).append("}");
		return builder.toString();
	}

}
