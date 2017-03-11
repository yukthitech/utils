package com.yukthitech.automation.test;

/**
 * Abstract base data provider for test cases.
 * @author akiran
 */
public abstract class AbstractDataProvider implements IDataProvider
{
	/**
	 * Name of the data provider.
	 */
	private String name;

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.test.IDataProvider#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name of the data provider.
	 *
	 * @param name the new name of the data provider
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
