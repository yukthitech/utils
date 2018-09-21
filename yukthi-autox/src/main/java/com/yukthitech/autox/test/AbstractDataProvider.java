package com.yukthitech.autox.test;

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
	
	/**
	 * Flag to enable expression parsing in data.
	 */
	private boolean parsingEnabled = false;

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

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.test.IDataProvider#isParsingDisabled()
	 */
	public boolean isParsingEnabled()
	{
		return parsingEnabled;
	}

	/**
	 * Sets the flag to disable expression parsing in data.
	 *
	 * @param parsingDisabled the new flag to disable expression parsing in data
	 */
	public void setParsingDisabled(boolean parsingDisabled)
	{
		this.parsingEnabled = parsingDisabled;
	}
}
