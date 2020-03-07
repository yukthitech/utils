package com.yukthitech.autox.doc;

/**
 * Abstract class for doc info objects to hold common properties.
 * @author akiran
 */
public class AbstractDocInfo
{
	/**
	 * Documentation generated for current info object.
	 */
	private String documentation;

	/**
	 * Gets the documentation generated for current info object.
	 *
	 * @return the documentation generated for current info object
	 */
	public String getDocumentation()
	{
		return documentation;
	}

	/**
	 * Sets the documentation generated for current info object.
	 *
	 * @param documentation the new documentation generated for current info object
	 */
	public void setDocumentation(String documentation)
	{
		this.documentation = documentation;
	}

	public boolean hasReturnInfo()
	{
		return false;
	}
	
	public boolean hasParameters()
	{
		return false;
	}
}
