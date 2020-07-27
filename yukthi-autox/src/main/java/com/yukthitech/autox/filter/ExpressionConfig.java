package com.yukthitech.autox.filter;

/**
 * Custom configuration that can be set during expression parsing.
 * @author akiran
 */
public class ExpressionConfig
{
	/**
	 * Initial value that can be passed to first expression part.
	 */
	private Object initValue;
	
	/**
	 * Default expected type for expression result type. 
	 */
	private Class<?> defaultExpectedType;

	/**
	 * Instantiates a new expression config.
	 *
	 * @param initValue the init value
	 * @param defaultExpectedType the default expected type
	 */
	public ExpressionConfig(Object initValue, Class<?> defaultExpectedType)
	{
		this.initValue = initValue;
		this.defaultExpectedType = defaultExpectedType;
	}

	/**
	 * Gets the initial value that can be passed to first expression part.
	 *
	 * @return the initial value that can be passed to first expression part
	 */
	public Object getInitValue()
	{
		return initValue;
	}

	/**
	 * Sets the initial value that can be passed to first expression part.
	 *
	 * @param initValue the new initial value that can be passed to first expression part
	 */
	public void setInitValue(Object initValue)
	{
		this.initValue = initValue;
	}

	/**
	 * Gets the default expected type for expression result type.
	 *
	 * @return the default expected type for expression result type
	 */
	public Class<?> getDefaultExpectedType()
	{
		return defaultExpectedType;
	}

	/**
	 * Sets the default expected type for expression result type.
	 *
	 * @param defaultExpectedType the new default expected type for expression result type
	 */
	public void setDefaultExpectedType(Class<?> defaultExpectedType)
	{
		this.defaultExpectedType = defaultExpectedType;
	}
}
