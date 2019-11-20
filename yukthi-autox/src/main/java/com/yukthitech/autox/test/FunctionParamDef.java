package com.yukthitech.autox.test;

/**
 * Represents definition of function parameter.
 * @author akiran
 */
public class FunctionParamDef
{
	/**
	 * Name of the function parameter.
	 */
	private String name;
	
	/**
	 * Description of the function parameter.
	 */
	private String description;
	
	/**
	 * Flag indicating if this parameter is required or not. Default is false.
	 */
	private boolean required = false;

	/**
	 * Gets the name of the function parameter.
	 *
	 * @return the name of the function parameter
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the function parameter.
	 *
	 * @param name the new name of the function parameter
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the description of the function parameter.
	 *
	 * @return the description of the function parameter
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of the function parameter.
	 *
	 * @param description the new description of the function parameter
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the flag indicating if this parameter is required or not. Default is false.
	 *
	 * @return the flag indicating if this parameter is required or not
	 */
	public boolean isRequired()
	{
		return required;
	}

	/**
	 * Sets the flag indicating if this parameter is required or not. Default is false.
	 *
	 * @param required the new flag indicating if this parameter is required or not
	 */
	public void setRequired(boolean required)
	{
		this.required = required;
	}
}
