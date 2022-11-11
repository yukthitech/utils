package com.yukthitech.autox.debug.common;

import java.io.Serializable;

/**
 * Used to send context attribute details to connected env.
 * @author akiran
 */
public class ContextAttributeDetails implements Serializable
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the attribute.
	 */
	private String name;

	/**
	 * Value of the attribute.
	 */
	private Object value;
	
	/**
	 * Instantiates a new context attribute details.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public ContextAttributeDetails(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets the name of the attribute.
	 *
	 * @return the name of the attribute
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the attribute.
	 *
	 * @param name the new name of the attribute
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the value of the attribute.
	 *
	 * @return the value of the attribute
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the attribute.
	 *
	 * @param value the new value of the attribute
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
}
