package com.yukthitech.autox.test;

import java.io.Serializable;

import com.yukthitech.autox.common.AutomationUtils;

/**
 * Parameter that can be passed during step group execution.
 * @author akiran
 */
public class StepGroupParam implements Serializable
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the group param.
	 */
	private String name;
	
	/**
	 * Value of the param.
	 */
	private Object value;
	
	/**
	 * Type to be coverted to.
	 */
	private String type;

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
	 * Fetches final result value. If needed type conversion will be done.
	 * @return converted value
	 */
	public Object getResultValue()
	{
		return AutomationUtils.convert(value, type);
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
	
	/**
	 * Gets the type to be coverted to.
	 *
	 * @return the type to be coverted to
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Sets the type to be coverted to.
	 *
	 * @param type the new type to be coverted to
	 */
	public void setType(String type)
	{
		this.type = type;
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
