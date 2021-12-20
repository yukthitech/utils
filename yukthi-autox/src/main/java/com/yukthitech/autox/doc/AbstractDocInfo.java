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
	 * Additional information about the element.
	 */
	private String additionalInfo;

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
	
	/**
	 * Sets the additional information about the element.
	 *
	 * @param additionalInfo
	 *            the new additional information about the element
	 */
	public void setAdditionalInfo(String additionalInfo)
	{
		this.additionalInfo = additionalInfo;
	}
	
	/**
	 * Gets the additional information about the element.
	 *
	 * @return the additional information about the element
	 */
	public String getAdditionalInfo()
	{
		return additionalInfo;
	}

	/**
	 * Checks for return info.
	 *
	 * @return true, if successful
	 */
	public boolean hasReturnInfo()
	{
		return false;
	}
	
	/**
	 * Checks for parameters.
	 *
	 * @return true, if successful
	 */
	public boolean hasParameters()
	{
		return false;
	}
}
