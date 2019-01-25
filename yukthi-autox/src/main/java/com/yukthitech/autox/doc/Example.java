package com.yukthitech.autox.doc;

/**
 * Represents an example with description.
 * @author akiran
 */
public class Example
{
	/**
	 * Description of example.
	 */
	private String description;
	
	/**
	 * Example text.
	 */
	private String content;

	/**
	 * Gets the description of example.
	 *
	 * @return the description of example
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of example.
	 *
	 * @param description the new description of example
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the example text.
	 *
	 * @return the example text
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * Sets the example text.
	 *
	 * @param content the new example text
	 */
	public void setContent(String content)
	{
		this.content = content;
	}
}
